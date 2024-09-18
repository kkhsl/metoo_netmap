package com.metoo.sqlite.gather.self;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IPv6Generator {

    public static void main(String[] args) {

        try {

            String address = "240b:8160:3008:700";

            // Complete the address
            InetAddress fullAddress = InetAddress.getByName(address + "::");

            // Print the full address
            System.out.println("Full Address: " + fullAddress.getHostAddress());

            // Calculate network segment
            String networkSegment = calculateNetworkSegment(address, 64);

            int numberOfAddresses = 10; // Specify the number of addresses to generate

            List<String> ipv6List = generateIPv6Addresses(networkSegment, numberOfAddresses);
            for (String s : ipv6List) {
                System.out.println(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String calculateNetworkSegment(String address, int prefixLength) throws UnknownHostException {

        // Assuming prefixLength is 64 for simplicity
        // Create a 128-bit bit array from the address
        String fullAddress = address + "::";
        String[] addressParts = fullAddress.split(":");
        StringBuilder networkSegment = new StringBuilder();

        // Append the first 4 blocks (64 bits)
        for (int i = 0; i < 4; i++) {
            networkSegment.append(addressParts[i]);
            if (i < 3) {
                networkSegment.append(":");
            }
        }

        // Append the rest of the blocks as zeros
        networkSegment.append(":").append("0000:0000:0000:0000");

        return networkSegment.toString() + "/" + prefixLength;
    }

    private static final SecureRandom random = new SecureRandom();

    public static List<String> generateIPv6Addresses(String networkSegment, int count) throws UnknownHostException {
        // Extract the network base address and prefix length
        String[] parts = networkSegment.split("/");
        String baseAddress = parts[0];
        int prefixLength = Integer.parseInt(parts[1]);

        // Convert base address to a BigInteger
        BigInteger base = ipToBigInteger(baseAddress);

        // Generate addresses
        List<String> ipv6List = new ArrayList<>();
        for (int i = 0; i < count; i++) {
//            BigInteger address = base.add(BigInteger.valueOf(i));
            BigInteger offset = new BigInteger(64 - prefixLength, random);
            BigInteger address = base.add(offset);
//            String addressString = bigIntegerToIPv6(address);
            String addressString = bigIntegerToIPv6(address);
//            System.out.println("Generated Address: " + addressString);
            ipv6List.add(addressString);
        }
        return ipv6List;
    }

    private static BigInteger ipToBigInteger(String ip) throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName(ip);
        byte[] bytes = inetAddress.getAddress();
        return new BigInteger(1, bytes);
    }

    private static String bigIntegerToIPv6(BigInteger bigInteger) {
        byte[] bytes = bigInteger.toByteArray();
        if (bytes.length < 16) {
            byte[] paddedBytes = new byte[16];
            System.arraycopy(bytes, 0, paddedBytes, 16 - bytes.length, bytes.length);
            bytes = paddedBytes;
        }
        try {
            InetAddress inetAddress = InetAddress.getByAddress(bytes);
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
