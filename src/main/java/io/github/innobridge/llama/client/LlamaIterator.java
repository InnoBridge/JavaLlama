package io.github.innobridge.llama.client;

import java.lang.annotation.Native;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This iterator is used by {@link LlamaModel#generate(InferenceParameters)}. In
 * addition to implementing {@link Iterator},
 * it allows to cancel ongoing inference (see {@link #cancel()}).
 */
public final class LlamaIterator<T extends LlamaOutput> implements Iterator<T> {

  private final LlamaModel model;
  private final int taskId;

  @Native
  @SuppressWarnings("FieldMayBeFinal")
  private boolean hasNext = true;

  LlamaIterator(LlamaModel model, InferenceParameters parameters) {
    this.model = model;
    parameters.setStream(true);
    taskId = model.requestCompletion(parameters.toString());
  }

  @Override
  public boolean hasNext() {
    return hasNext;
  }

  @Override
  public T next() {
    if (!hasNext) {
      throw new NoSuchElementException();
    }
    LlamaOutput output = model.receiveCompletion(taskId);
    hasNext = !output.stop;
    return (T) output;
  }

  /**
   * Cancel the ongoing generation process.
   */
  public void cancel() {
    model.cancelCompletion(taskId);
    hasNext = false;
  }
}
