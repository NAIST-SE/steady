package com.sap.psr.vulas.shared.util;

/**
 * <p>ProgressTracker class.</p>
 *
 */
public class ProgressTracker {

	private double total = -1;
	private double current =  0;
	
	/**
	 * <p>Constructor for ProgressTracker.</p>
	 *
	 * @param _total a double.
	 */
	public ProgressTracker(double _total) {
		this.total = _total;
	}
	
	/**
	 * Returns the increase in percent of the total.
	 *
	 * @param _by a double.
	 * @throws java.lang.IllegalArgumentException
	 * @return a double.
	 */
	public double increase(double _by) throws IllegalArgumentException {
		if(this.current+_by>this.total)
			throw new IllegalArgumentException("Total exceeded");
		this.current += _by;
		return _by/this.total;
	}
	
	/**
	 * <p>Getter for the field <code>current</code>.</p>
	 *
	 * @return a double.
	 */
	public double getCurrent() { return this.current; }
	
	/**
	 * <p>Getter for the field <code>total</code>.</p>
	 *
	 * @return a double.
	 */
	public double getTotal() { return this.total; }
	
	/*
	 * Returns the completion in percent.
	 */
	/**
	 * <p>getCompletion.</p>
	 *
	 * @return a double.
	 */
	public double getCompletion() {
		return 100 * this.current / this.total;
	}
	
	/*
	 * Returns the completion in percent.
	 */
	/**
	 * <p>getCompletionAsLong.</p>
	 *
	 * @return a long.
	 */
	public long getCompletionAsLong() {
		return (long)Math.floor(this.getCompletion());
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		final StringBuffer b = new StringBuffer();
		b.append(StringUtil.padLeft(String.format("%.2f", this.getCompletion()), 6)).append("%");
		b.append(" (").append(StringUtil.padLeft(this.current, Double.toString(this.total).length())).append(" / ").append(this.total).append(")");
		return b.toString();
	}
}
