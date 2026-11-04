package ma.emsi.othmanemoussawi.tp2othmanemoussawi.llm;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.enterprise.context.Dependent;

import java.io.Serializable;

/**
 * Classe métier qui gère l'interface avec le LLM (Gemini) en utilisant LangChain4j.
 */
@Dependent
public class LlmClient implements Serializable {

    /**
     * Rôle choisi par l'utilisateur pour l'assistant IA.
     */
    private String systemRole;
    /**
     * Assistant implémenté par LangChain4j (AiServices).
     */
    private Assistant assistant;
    /**
     * Mémoire de l'assistant pour garder l'historique de la conversation.
     */
    private ChatMemory chatMemory;

    public LlmClient() {
        // Récupère la clé secrète pour travailler avec l'API du LLM
        String llmKey = System.getenv("GEMINI_KEY");

        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(llmKey)
                .modelName("gemini-3.5-flash-lite")
                .build();

        this.chatMemory = MessageWindowChatMemory.withMaxMessages(20);
        this.assistant = AiServices.builder(Assistant.class)
                                   .chatModel(model)
                                   .chatMemory(chatMemory)
                                   .build();
    }

    /**
     * Met le rôle système. Vide la mémoire avant d'y ajouter le rôle système.
     *
     * @param systemRole le rôle système.
     */
    public void setSystemRole(String systemRole) {
        this.systemRole = systemRole;
        this.chatMemory.clear();
        this.chatMemory.add(SystemMessage.from(systemRole));
    }

    /**
     * Envoie une question au LLM et retourne sa réponse.
     *
     * @param question la question.
     * @return la réponse du LLM.
     */
    public String envoyerRequete(String question) {
        return this.assistant.chat(question);
    }
}
