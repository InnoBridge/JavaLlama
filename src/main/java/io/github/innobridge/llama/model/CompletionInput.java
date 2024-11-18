package io.github.innobridge.llama.model;

public record CompletionInput(
        String prompt,
        Integer nPredict,
        Integer topK,
        Float topP,
        Float temperature,
        Float repeatPenalty,
        Boolean cachePrompt
) {
    public static class Builder {
        private String prompt;
        private Integer nPredict;
        private Integer topK;
        private Float topP;
        private Float temperature;
        private Float repeatPenalty;
        private Boolean cachePrompt;

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

        public CompletionInput build() {
            if (prompt == null || prompt.isEmpty()) {
                throw new IllegalArgumentException("Prompt is required");
            }
            return new CompletionInput(
                prompt,
                nPredict,
                topK,
                topP,
                temperature,
                repeatPenalty,
                cachePrompt
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
