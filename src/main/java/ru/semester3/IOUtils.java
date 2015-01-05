package ru.semester3;

import akka.actor.Address;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class IOUtils {
    public static Field loadFromFile(File source) throws IOException {
        try (InputStream input = new FileInputStream(source)) {
            int width = readInt(input);
            int height = readInt(input);

            byte[] rawCells = new byte[(width * height) / 8];
            input.read(rawCells);

            return new Field(width, height, toBoolean(rawCells));
        }
    }

    private static int readInt(InputStream input) throws IOException {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int nextByte = input.read();
            nextByte <<= i * 8;
            value |= nextByte;
        }

        return value;
    }

    private static boolean[] toBoolean(byte[] source) {
        boolean[] dest = new boolean[source.length * 8];

        int i = 0;
        for (byte b : source) {
            for (int j = 0; j < 8; j++) {
                int mask = 0b10000000 >>> j;
                dest[i++] = (b & mask) == mask;
            }
        }

        return dest;
    }

    public static void saveToFile(File file, Field field) throws IOException {
        try (OutputStream output = new FileOutputStream(file)) {
            writeInt(output, field.width());
            writeInt(output, field.height());
            output.write(toByte(field.cells()));
        }
    }

    private static void writeInt(OutputStream output, int value) throws IOException {
        for (int i = 0; i < 4; i++) {
            output.write(value);
            value >>>= 8;
        }
    }

    private static byte[] toByte(boolean[] source) {
        byte[] dest = new byte[source.length / 8];

        for (int i = 0; i < dest.length; i++) {
            byte b = 0;

            for (int j = 0; j < 8; j++) {
                if (source[i * 8 + j]) {
                    b |= 0b10000000 >>> j;
                }
            }

            dest[i] = b;
        }

        return dest;
    }

    public static Address[] getAddresses(String location) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(location), StandardCharsets.UTF_8);

        Address[] result = new Address[lines.size()];

        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(" ");
            Address a = new Address("akka.tcp", parts[0], parts[1], Integer.parseInt(parts[2]));
            result[i] = a;
        }

        return result;
    }
}
