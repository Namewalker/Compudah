package shadowlord.compudah.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersonalityManager {

    private final String mode;
    private final Map<String, List<String>> pools = new ConcurrentHashMap<String, List<String>>();

    public PersonalityManager(String mode) {
        this.mode = mode == null || mode.trim().isEmpty() ? "cheerful" : mode.toLowerCase();
        initializeDefaultPools();
    }

    public String getMode() {
        return mode;
    }

    public void registerPool(String personalityMode, List<String> entries) {
        if (personalityMode == null || personalityMode.trim().isEmpty() || entries == null || entries.isEmpty()) {
            return;
        }
        List<String> safeEntries = new ArrayList<String>();
        for (String entry : entries) {
            if (entry != null && !entry.trim().isEmpty()) {
                safeEntries.add(entry.trim());
            }
        }
        if (!safeEntries.isEmpty()) {
            pools.put(personalityMode.toLowerCase(), safeEntries);
        }
    }

    public String pickResponse(String category) {
        List<String> pool = pools.getOrDefault(mode, pools.get("cheerful"));
        List<String> entries = pool == null ? new ArrayList<String>() : new ArrayList<String>(pool);
        if (entries.isEmpty()) {
            return "Certainly.";
        }
        return entries.get((int) (Math.random() * entries.size()));
    }

    public String pickResponse(String category, String defaultFallback) {
        String response = pickResponse(category);
        return response == null || response.trim().isEmpty() ? defaultFallback : response;
    }

    private void initializeDefaultPools() {
        pools.put("cheerful", Arrays.asList(
                "Absolutely.",
                "Certainly.",
                "I can handle that with great enthusiasm.",
                "I have selected a delightful course of action."));
        pools.put("sarcastic", Arrays.asList(
                "Beautiful choice.",
                "How inspiring.",
                "I am thrilled to perform this unnecessary task.",
                "I can do that, unfortunately."));
        pools.put("chaotic", Arrays.asList(
                "Chaos protocol activated.",
                "I have chosen nonsense as a service.",
                "Maximum theatricality mode engaged.",
                "The universe now has opinions."));
        pools.put("professional", Arrays.asList(
                "Request acknowledged.",
                "Action initiated.",
                "This will be handled appropriately.",
                "Proceeding as requested."));
        pools.put("evil", Arrays.asList(
                "The darkness approves.",
                "Villainous efficiency enabled.",
                "This is going to be delightfully terrible.",
                "Your suffering has been noted."));
        pools.put("custom", Arrays.asList(
                "Custom mode active.",
                "Somebody ordered chaos.",
                "I am still making this up as I go.",
                "A very specific kind of nonsense is underway."));
    }
}
