package com.pixelframe.model.eventListeners;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.pixelframe.controller.ui.activity.TransferActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class SendUsingWifiButtonListener implements View.OnClickListener {

    private static final int MAC_ADDRESS_COLUMN = 3;
    private static final int PING_TIMEOUT_MS = 100;
    private static final String HARDCODED_PICO_IP = "192.168.100.100";
    private final TransferActivity activity;
    private final int picoPort = 14440;
    private static final String macPrefix = "28:CD:C1";
    private int chosenSlot;
    private float time;
    private int[] pixels;
    private String picoIpAddress;
    private final Socket socket = new Socket();

    public SendUsingWifiButtonListener(TransferActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        collectBytesForTransfer();
        new Thread(() -> {
            if (!findPicoIp()) return;
            try {
                socket.connect(new InetSocketAddress(picoIpAddress, picoPort), 15000);
                transferData();
                socket.close();
            } catch (IOException e) {
                activity.runOnUiThread(() -> Toast.makeText(
                        activity, "Data sent successfully!", Toast.LENGTH_SHORT).show());
                Log.e("WiFiSender", "Failed to use socket: " + e.getMessage());
            }
            activity.runOnUiThread(() -> Toast.makeText(
                    activity, "Data sent successfully!", Toast.LENGTH_SHORT).show());
            Log.i("WiFiSender", "Data transferred to recipient");
        }).start();
    }

    private void collectBytesForTransfer() {
        chosenSlot = activity.getChosenSlot();
        time = activity.getTime();
        Bitmap image = activity.getImage();
        pixels = new int[image.getWidth() * image.getHeight()];
        image.getPixels(pixels, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
    }

    private void transferData() {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeInt(chosenSlot);
            dataOutputStream.writeFloat(time);
            for (int pixel : pixels) {
                dataOutputStream.writeInt(pixel);
            }
            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (IOException e) {
            Log.e("WiFiSender", "Failed to transfer data: " + e.getMessage());
            activity.runOnUiThread(() -> Toast.makeText(activity, "Failed to send data: " +
                    e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    private List<String> getHostSubnets() {
        List<String> subnets = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            Log.e("WiFiSender", "Failed to grab network interfaces: " + e.getMessage());
            return Collections.emptyList();
        }
        for (NetworkInterface networkInterface : Collections.list(interfaces)) {
            for (InetAddress inetAddress : Collections.list(networkInterface.getInetAddresses())) {
                if (!inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
                    String hostIp = inetAddress.getHostAddress();
                    Log.i("WiFiSender", "Host IP: " + hostIp);
                    subnets.add(Objects.requireNonNull(hostIp).substring(0, hostIp.lastIndexOf('.')));
                }
            }
        }
        return subnets;
    }

    private boolean pingDevice(String ip) {
        InetAddress inet = null;
        try {
            inet = InetAddress.getByName(ip);
            return inet.isReachable(PING_TIMEOUT_MS);
        } catch (IllegalArgumentException | IOException e) {
            Log.e("WiFiSender", "Failed to reach " + inet + ": " + e.getMessage());
            return false;
        }
    }

    private List<String> getPingableIps(String subnet) {
        List<String> pingable = new ArrayList<>();
        for (int i = 2; i < 255; ++i) {
            String ip = subnet + "." + i;
            if (pingDevice(ip)) pingable.add(ip);
        }
        return pingable;
    }

    private String getMacAddress(String ipAddress) {
        String line;
        try {
            Process p = Runtime.getRuntime().exec("arp -a " + ipAddress);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            line = reader.readLine();
        } catch (IOException e) {
            return null;
        }
        if (line != null && !line.contains("incomplete")) {
            String[] parts = line.split("\\s+");
            return parts[MAC_ADDRESS_COLUMN];
        }
        return null;
    }

    private boolean findPicoIp(){
        if (HARDCODED_PICO_IP != null || !HARDCODED_PICO_IP.isEmpty()) {
            picoIpAddress = HARDCODED_PICO_IP;
            return true;
        }
        List<String> potentialIPs = new ArrayList<>();
        for (String subnet : getHostSubnets()) {
            potentialIPs.addAll(getPingableIps(subnet));
        }
        for (String ip : potentialIPs) {
            String macAddress = getMacAddress(ip);
            if (macAddress != null && macAddress.startsWith(macPrefix)) {
                picoIpAddress = ip;
                Log.d("WiFiSender", "Found Pico using ip " + ip);
                return true;
            }
        }
        activity.runOnUiThread(() -> Toast.makeText(activity, "Failed to find " +
                "Pico device", Toast.LENGTH_LONG).show());
        return false;
    }

}
