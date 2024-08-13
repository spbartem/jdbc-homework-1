package ru.productstar.service;

import org.springframework.stereotype.Service;
import ru.productstar.model.Contact;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContactReader {

    public List<Contact> readFromFile(Path filePath) {
        List<Contact> contacts = new ArrayList<>();
        String delimiter = ",";
        String line;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath.toFile()))) {
            while ((line = bufferedReader.readLine()) != null) {
                String[] fields = line.split(delimiter);
                Contact contact = new Contact(
                        fields[0],
                        fields[1],
                        fields[2],
                        fields[3]);
                contacts.add(contact);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return contacts;
    }
}
