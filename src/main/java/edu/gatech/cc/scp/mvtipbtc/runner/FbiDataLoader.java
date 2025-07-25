package edu.gatech.cc.scp.mvtipbtc.runner;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FbiDataLoader implements ApplicationRunner {

    private final VectorStore vectorStore;

    // Ensure this path points to your new CSV file in the resources folder
    @Value("classpath:data/FBI Lazarus Group Wallets - Sheet1.csv")
    private Resource fbiDataFile;

    public FbiDataLoader(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("Loading FBI Bitcoin data into a single vector store document...");

        List<String> bitcoinAddresses = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fbiDataFile.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            // Skip header line
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                // Ensure the row has enough columns and the network is Bitcoin
                if (parts.length >= 2 && "Bitcoin".equalsIgnoreCase(parts[1].trim())) {
                    bitcoinAddresses.add(parts[0].trim());
                }
            }
        }

        if (bitcoinAddresses.isEmpty()) {
            System.out.println("No Bitcoin addresses found in the FBI data file.");
            return;
        }

        // Join all the collected Bitcoin addresses into a single string
        String combinedContent = String.join(", ", bitcoinAddresses);

        // Create a single document containing all the addresses
        Document singleDocument = new Document(
            "FBI Monitored Bitcoin Addresses: " + combinedContent,
            Map.of("source", "FBI", "type", "Lazarus Group Watchlist")
        );

        // Add the single, consolidated document to the vector store
        vectorStore.add(List.of(singleDocument));
        System.out.println("Successfully loaded " + bitcoinAddresses.size() + " FBI Bitcoin addresses into a single document.");
    }
}
