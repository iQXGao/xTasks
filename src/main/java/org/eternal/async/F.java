package org.eternal.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/***
 * 
 * 2018.03.05
 *
 */

public interface F {

	static F.Executor getDefExecutor() {
		return FConfig.getDefExecutor();
	}

	interface Callback<T> extends FutureCallback<T> {

	}

	final class Executor {

		final ListeningExecutorService executor;

		Executor(ExecutorService executor) {
			this.executor = MoreExecutors.listeningDecorator(executor);
		}

		public <T> Listenable<T> submit(Callable<T> task) {

			return new Listenable<T>(executor.submit(task));
		}

		void shutdown() {
			this.executor.shutdown();
		}

	}

	final class Listenable<T> {

		ListenableFuture<T> future;

		Listenable(ListenableFuture<T> future) {
			this.future = future;
		}

		public T get() throws InterruptedException, ExecutionException {
			return future.get();
		}
	}

	interface Function<I, O> {
		O apply(I input);
	}

	// ------------------------

	static <T> F.Listenable<T> submit(Callable<T> task) {
		return FConfig.getDefExecutor().submit(task);
	}

	static F.Executor generate(ExecutorService executor) {
		return new F.Executor(executor);
	}

	static <T, C extends Callback<T>> void async(Listenable<T> listenable, C callback) {
		Futures.addCallback(listenable.future, callback);
	}

	static <T> Listenable<T> immediate(T value) {

		return new Listenable<>(Futures.immediateFuture(value));
	}

	static <T> Listenable<List<T>> all(Iterable<Listenable<T>> lfs) {

		List<ListenableFuture<T>> fs = new ArrayList<>();
		for (Listenable<T> lf : lfs) {
			fs.add(lf.future);
		}
		return new Listenable<>(Futures.allAsList(fs));
	}

	@SuppressWarnings("unchecked")
	static <T, O> Listenable<T> transform(Listenable<T> input, Function<? super T, O> function) {
		return new Listenable<T>((ListenableFuture<T>) Futures.transformAsync(input.future, new AsyncFunction<T, O>() {
			@Override
			public ListenableFuture<O> apply(T arg0) throws Exception {
				return Futures.immediateFuture(function.apply(arg0));
			}
		}));
	}

	static void shutdown(Executor executor) {
		if (!executor.equals(FConfig.getDefExecutor())) {
			executor.shutdown();
		}
	}

}
