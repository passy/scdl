package net.rdrei.android.option;

public class Some<A> extends Option<A> {

	private final A value;

	Some(A value) {
		this.value = value;
	}

	@Override
	public <B> Option<B> map(Func1<A, B> f) {
		return some(f.apply(value));
	}

	@Override
	public Option<A> call(Consumer1<A> f) {
		f.accept(value);
		return this;
	}

	@Override
	public <B> Option<B> flatMap(Func1<A, Option<B>> f) {
		return f.apply(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Option<A> filter(Predicate<? super A> predicate) {
		if (predicate.test(value)) {
			return this;
		} else {
			return None.NONE;
		}
	}

	@Override
	public A getOrElse(A def) {
		return value;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public A get() {
		return value;
	}
}