package io.github.innobridge.llama.model;

import io.github.innobridge.llama.client.args.Sampler;
import io.github.innobridge.llama.client.InferenceParameters;

/**
 * Input parameters for both completion and generation operations.
 * All parameters except prompt are optional and have sensible defaults.
 */
public record InferenceInput(
        String prompt,
        String inputPrefix,
        String inputSuffix,
        Integer nPredict,
        Integer topK,
        Float topP,
        Float minP,
        Float tfsZ,
        Float typicalP,
        Float temperature,
        Float dynatempRange,
        Float dynatempExponent,
        Integer repeatLastN,
        Float repeatPenalty,
        Float frequencyPenalty,
        Boolean cachePrompt,
        Boolean stream,
        Integer nProbs,
        String[] stopStrings,
        Boolean useChatTemplate,
        int[] penaltyPrompt,
        Float mirostatEta,
        Float mirostatTau,
        Sampler[] samplers
) {
    public static class Builder {
        private String prompt;
        private String inputPrefix;
        private String inputSuffix;
        private Integer nPredict;
        private Integer topK;
        private Float topP;
        private Float minP;
        private Float tfsZ;
        private Float typicalP;
        private Float temperature;
        private Float dynatempRange;
        private Float dynatempExponent;
        private Integer repeatLastN;
        private Float repeatPenalty;
        private Float frequencyPenalty;
        private Boolean cachePrompt;
        private Boolean stream;
        private Integer nProbs;
        private String[] stopStrings;
        private Boolean useChatTemplate;
        private int[] penaltyPrompt;
        private Float mirostatEta;
        private Float mirostatTau;
        private Sampler[] samplers;

        public Builder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public Builder inputPrefix(String inputPrefix) {
            this.inputPrefix = inputPrefix;
            return this;
        }

        public Builder inputSuffix(String inputSuffix) {
            this.inputSuffix = inputSuffix;
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

        public Builder minP(float minP) {
            this.minP = minP;
            return this;
        }

        public Builder tfsZ(float tfsZ) {
            this.tfsZ = tfsZ;
            return this;
        }

        public Builder typicalP(float typicalP) {
            this.typicalP = typicalP;
            return this;
        }

        public Builder temperature(float temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder dynatempRange(float dynatempRange) {
            this.dynatempRange = dynatempRange;
            return this;
        }

        public Builder dynatempExponent(float dynatempExponent) {
            this.dynatempExponent = dynatempExponent;
            return this;
        }

        public Builder repeatLastN(int repeatLastN) {
            this.repeatLastN = repeatLastN;
            return this;
        }

        public Builder repeatPenalty(float repeatPenalty) {
            this.repeatPenalty = repeatPenalty;
            return this;
        }

        public Builder frequencyPenalty(float frequencyPenalty) {
            this.frequencyPenalty = frequencyPenalty;
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

        public Builder nProbs(int nProbs) {
            this.nProbs = nProbs;
            return this;
        }

        public Builder stopStrings(String... stopStrings) {
            this.stopStrings = stopStrings;
            return this;
        }

        public Builder useChatTemplate(boolean useChatTemplate) {
            this.useChatTemplate = useChatTemplate;
            return this;
        }

        public Builder penaltyPrompt(int[] penaltyPrompt) {
            this.penaltyPrompt = penaltyPrompt;
            return this;
        }

        public Builder mirostatEta(float mirostatEta) {
            this.mirostatEta = mirostatEta;
            return this;
        }

        public Builder mirostatTau(float mirostatTau) {
            this.mirostatTau = mirostatTau;
            return this;
        }

        public Builder samplers(Sampler... samplers) {
            this.samplers = samplers;
            return this;
        }

        public InferenceInput build() {
            if (prompt == null || prompt.isEmpty()) {
                throw new IllegalArgumentException("Prompt is required");
            }
            return new InferenceInput(
                prompt,
                inputPrefix,
                inputSuffix,
                nPredict,
                topK,
                topP,
                minP,
                tfsZ,
                typicalP,
                temperature,
                dynatempRange,
                dynatempExponent,
                repeatLastN,
                repeatPenalty,
                frequencyPenalty,
                cachePrompt,
                stream,
                nProbs,
                stopStrings,
                useChatTemplate,
                penaltyPrompt,
                mirostatEta,
                mirostatTau,
                samplers
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer nProbs() {
        return nProbs;
    }

    public String[] stopStrings() {
        return stopStrings;
    }

    public Boolean useChatTemplate() {
        return useChatTemplate;
    }

    public int[] penaltyPrompt() {
        return penaltyPrompt;
    }

    public Float mirostatEta() {
        return mirostatEta;
    }

    public Float mirostatTau() {
        return mirostatTau;
    }

    public Sampler[] samplers() {
        return samplers;
    }

    public InferenceParameters toInferenceParameters() {
        if (prompt == null || prompt.isEmpty()) {
            throw new IllegalArgumentException("Prompt is required");
        }

        InferenceParameters params = new InferenceParameters(prompt);

        if (cachePrompt != null) {
            params.setCachePrompt(cachePrompt);
        }
        if (nPredict != null) {
            params.setNPredict(nPredict);
        }
        if (topK != null) {
            params.setTopK(topK);
        }
        if (topP != null) {
            params.setTopP(topP);
        }
        if (temperature != null) {
            params.setTemperature(temperature);
        }
        if (repeatPenalty != null) {
            params.setRepeatPenalty(repeatPenalty);
        }
        if (stream != null) {
            params.setStream(stream);
        }
        if (inputPrefix != null) {
            params.setInputPrefix(inputPrefix);
        }
        if (inputSuffix != null) {
            params.setInputSuffix(inputSuffix);
        }
        if (minP != null) {
            params.setMinP(minP);
        }
        if (tfsZ != null) {
            params.setTfsZ(tfsZ);
        }
        if (typicalP != null) {
            params.setTypicalP(typicalP);
        }
        if (dynatempRange != null) {
            params.setDynamicTemperatureRange(dynatempRange);
        }
        if (dynatempExponent != null) {
            params.setDynamicTemperatureExponent(dynatempExponent);
        }
        if (repeatLastN != null) {
            params.setRepeatLastN(repeatLastN);
        }
        if (frequencyPenalty != null) {
            params.setFrequencyPenalty(frequencyPenalty);
        }
        if (nProbs != null) {
            params.setNProbs(nProbs);
        }
        if (stopStrings != null) {
            params.setStopStrings(stopStrings);
        }
        if (useChatTemplate != null) {
            params.setUseChatTemplate(useChatTemplate);
        }
        if (penaltyPrompt != null) {
            params.setPenaltyPrompt(penaltyPrompt);
        }
        if (mirostatEta != null) {
            params.setMiroStatEta(mirostatEta);
        }
        if (mirostatTau != null) {
            params.setMiroStatTau(mirostatTau);
        }
        if (samplers != null) {
            params.setSamplers(samplers);
        }

        return params;
    }
}
