package io.github.innobridge.llama.model;

public interface GenerationCallback {
    /**
     * Called when a new token is generated
     * @param response The generated token or text
     */
    void onResponse(String response);

    /**
     * Called when the generation is complete
     */
    void onComplete();

    /**
     * Called when an error occurs during generation
     * @param t The throwable that caused the error
     */
    void onError(Throwable t);
}
