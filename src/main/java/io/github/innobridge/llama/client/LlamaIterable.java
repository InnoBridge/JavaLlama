package io.github.innobridge.llama.client;

import org.jetbrains.annotations.NotNull;

/**
 * An iterable used by {@link LlamaModel#generate(InferenceParameters)} that
 * specifically returns a {@link LlamaIterator}.
 */
@FunctionalInterface
public interface LlamaIterable<T extends LlamaOutput> extends Iterable<T> {

  @NotNull
  @Override
  LlamaIterator<T> iterator();

}
