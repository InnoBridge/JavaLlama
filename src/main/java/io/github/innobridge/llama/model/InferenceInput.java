package io.github.innobridge.llama.model;

/**
 * Input parameters for both completion and generation operations.
 * All parameters except prompt are optional and have sensible defaults.
 */
public record InferenceInput(
        String prompt,
        Integer nPredict,
        Integer topK,
        Float topP,
        Float temperature,
        Float repeatPenalty,
        Boolean cachePrompt,
        Boolean stream
) {
    public static class Builder {
        private String prompt;
        private Integer nPredict;
        private Integer topK;
        private Float topP;
        private Float temperature;
        private Float repeatPenalty;
        private Boolean cachePrompt;
        private Boolean stream;

        public Builder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public Builder nPredict(int nPredict) {
            this.nPredict = nPredict;
            return this;
        }

        public Builder topK(int topK) {
            this.topK = topK;
            return this;
        }

        public Builder topP(float topP) {
            this.topP = topP;
            return this;
        }

        public Builder temperature(float temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder repeatPenalty(float repeatPenalty) {
            this.repeatPenalty = repeatPenalty;
            return this;
        }

        public Builder cachePrompt(boolean cachePrompt) {
            this.cachePrompt = cachePrompt;
            return this;
        }

        public Builder stream(boolean stream) {
            this.stream = stream;
            return this;
        }

        public InferenceInput build() {
            if (prompt == null || prompt.isEmpty()) {
                throw new IllegalArgumentException("Prompt is required");
            }
            return new InferenceInput(
                prompt,
                nPredict,
                topK,
                topP,
                temperature,
                repeatPenalty,
                cachePrompt,
                stream
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Convenience method to create a completion input (non-streaming).
     */
    public static InferenceInput completion(String prompt) {
        return builder().prompt(prompt).stream(false).build();
    }

    /**
     * Convenience method to create a generation input (streaming).
     */
    public static InferenceInput generation(String prompt) {
        return builder().prompt(prompt).stream(true).build();
    }
}
