package com.codeday.productivity.util;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CustomIdGenerator implements IdGenerator {

    // An atomic counter that ensures thread safety
    private static final AtomicInteger counter = new AtomicInteger(0);

    // A machine-specific prefix, which can be a hash of the machine's IP address, MAC address, etc.
    private static final int machinePrefix = calculateMachinePrefix();

    @Override
    public int generateId() {
        // Create the ID using the machine prefix and the next value from the counter
        return (machinePrefix << 16) + counter.incrementAndGet();
    }

    protected static int calculateMachinePrefix() {
        try {
            // Get the local machine's IP address
            InetAddress ip = InetAddress.getLocalHost();
            // Convert the IP address to a byte array
            byte[] bytes = ip.getAddress();
            // Use the bytes to create an integer value
            int result = 0;
            for (byte b : bytes) {
                result = (result << 8) + (b & 0xFF);
            }
            // Use the lower 16 bits of the result as the prefix
            return result & 0xFFFF;
        } catch (UnknownHostException e) {
            // If the IP address can't be determined, fall back to a default value
            return 42;
        }
    }
}

