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
    private static final String HARDCODED_PICO_IP = null;
    private final TransferActivity activity;
    private final int picoPort = 14440;
    private static final List<String> raspberryMacPrefixes = List.of(
            "28:CD", "B8:27", "D8:3A", "DC:A6:32", "E4:5F:01"
    );
    private int chosenSlot;
    private float time;
    private int[] pixels;
    private String picoIpAddress;
    private final Socket socket = new Socket();
    private boolean isScanning = false;

    public SendUsingWifiButtonListener(TransferActivity activity) {
        this.activity = activity;
        if (!findPicoInArpTable()) {
            Log.d("WiFiSender", "Pico not found in ARP, starting async scan...");
            startAsyncScan();
        }
    }

    @Override
    public void onClick(View v) {
        collectBytesForTransfer();
        new Thread(() -> {
            if (!findPicoIp()) return;
            Log.d("WiFiSender", "Pico found");
            try {
                socket.connect(new InetSocketAddress(picoIpAddress, picoPort), 15000);
                transferData();
                socket.close();
            } catch (IOException e) {
                activity.runOnUiThread(() -> Toast.makeText(
                        activity, "Failed to send picture", Toast.LENGTH_SHORT).show());
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

    private boolean findPicoInArpTable() {
        try {
            Process p = Runtime.getRuntime().exec("ip neigh");
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 4 && raspberryMacPrefixes.stream().anyMatch(parts[4]::startsWith)) {
                    picoIpAddress = parts[0];  // Zapisz IP Pico
                    Log.d("WiFiSender", "Found Pico in ARP table with IP: " + picoIpAddress);
                    return true;
                }
            }
        } catch (IOException e) {
            Log.e("WiFiSender", "Error while checking ARP table: " + e.getMessage());
        }
        return false;
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
        if (HARDCODED_PICO_IP != null && !HARDCODED_PICO_IP.isEmpty()) {
            picoIpAddress = HARDCODED_PICO_IP;
            return true;
        }
        if (findPicoInArpTable()) {
            return true;
        }
        if (isScanning) {
            activity.runOnUiThread(() -> Toast.makeText(activity, "Searching for Pico still " +
                            "on...", Toast.LENGTH_SHORT).show());
            return false;
        }
        Log.d("WiFiSender", "Starting new scan for Pico...");
        startAsyncScan();
        activity.runOnUiThread(() -> Toast.makeText(activity, "Searching for Pico started",
                Toast.LENGTH_SHORT).show());
        return false;
    }

    private void startAsyncScan() {
        isScanning = true;
        new Thread(() -> {
            List<String> potentialIPs = new ArrayList<>();
            for (String subnet : getHostSubnets()) {
                Log.d("WiFiSender", "Scanning subnet: " + subnet);
                potentialIPs.addAll(getPingableIps(subnet));
            }
            for (String ip : potentialIPs) {
                String macAddress = getMacAddress(ip);
                Log.d("WiFiSender", "Checking IP " + ip + " if MAC matches RPi...");
                if (macAddress != null
                        && raspberryMacPrefixes.stream().anyMatch(macAddress::startsWith)) {
                    picoIpAddress = ip;
                    Log.d("WiFiSender", "Found Pico using IP: " + ip);
                    break;
                }
            }
            isScanning = false;
        }).start();
    }

}
