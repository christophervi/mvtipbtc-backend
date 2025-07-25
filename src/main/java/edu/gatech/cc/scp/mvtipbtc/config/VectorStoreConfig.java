package edu.gatech.cc.scp.mvtipbtc.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {

    /**
     * Creates a ChatClient bean that is pre-configured with a RAG advisor.
     * This makes it easy to inject a RAG-enabled client anywhere in the application.
     * @param chatModel The base ChatModel (e.g., OpenAiChatModel).
     * @param vectorStore The VectorStore to use for retrieval.
     * @return A configured ChatClient instance.
     */
    /*@Bean
    public ChatClient chatClient(org.springframework.ai.chat.model.ChatModel chatModel, VectorStore vectorStore) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
                .build();
    }*/
}
