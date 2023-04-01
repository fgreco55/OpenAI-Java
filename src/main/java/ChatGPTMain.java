import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.image.CreateImageRequest;

public class ChatGPTMain {

    private final static String CHATGPTURL = "https://api.openai.com/v1/chat/completions";
    private final static String CONTENT_TYPE = "Content-Type: application/json";
    private final static String APIKEY = "secret key";                          // real key is in a property
    private final static String DEFAULT_MODEL = "gpt-3.5-turbo";                // current ChatGPT model
    private final static float DEFAULT_TEMP = 0.2f;                             // focused and deterministic
    private final static int DEFAULT_MAX_TOKENS = 10;                           // keep result short and sweet
    private final static String DEFAULT_CONFIG = "/Users/fgreco/src/OpenAI-Java/src/main/resources/chatgpt.properties";

    public static void main(String[] args) throws IOException {
        Properties prop = getConfigProperties(DEFAULT_CONFIG);
        String token = prop.getProperty("chatgpi.apikey");

        OpenAiService service = new OpenAiService(token);

        // System.out.println("\nCreating completion...");
        System.out.println("Streaming chat completion...");
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are a sarcastic comedian and will speak as such.");
        messages.add(systemMessage);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .n(1)
                .maxTokens(500)
                .logitBias(new HashMap<>())
                .stream(false)
                .build();

        List<ChatCompletionChunk> chunks = new ArrayList<>();
        
        //service.streamChatCompletion(chatCompletionRequest).blockingForEach(chunks::add);
        List<ChatCompletionChoice> choices = service.createChatCompletion(chatCompletionRequest).getChoices();

        System.out.println("size of response: " + chunks.size());
        //System.out.println("OUTPUT:  " + chunks.get(5));
        displayResponse(choices);
        service.shutdownExecutor();

    }

    static void displayResponse(List<ChatCompletionChoice> res) {
        for (ChatCompletionChoice s: res) {
            System.out.println(s.getMessage().getContent());
        }
    }
    static Properties getConfigProperties(String fname) throws IOException {
        Properties prop = new Properties();
        InputStream in = new FileInputStream(fname);

        prop.load(in);

        for (Enumeration e = prop.propertyNames(); e.hasMoreElements(); ) {
            String key = e.nextElement().toString();
            System.out.println(key + " = " + prop.getProperty(key));
        }
        return prop;
    }
}
