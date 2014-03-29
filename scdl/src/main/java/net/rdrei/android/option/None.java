package net.rdrei.android.option;

import java.util.NoSuchElementException;

public class None<A> extends Option<A> {

	None() {
	}

	@Override
	public <B> Option<B> map(Func1<A, B> f) {
		return NONE;
	}

	@Override
	public Option<A> call(Consumer1<A> f) {
		return this;
	}

	@Override
	public <B> Option<B> flatMap(Func1<A, Option<B>> f) {
		return NONE;
	}

	@Override
	public Option<A> filter(Predicate<? super A> predicate) {
		return NONE;
	}

	@Override
	public A getOrElse(A def) {
		return def;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public A get() {
		throw new NoSuchElementException();
	}
}