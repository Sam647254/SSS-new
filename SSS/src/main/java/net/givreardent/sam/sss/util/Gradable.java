package net.givreardent.sam.sss.util;

public interface Gradable {
	double getOutOf();
	double getScore();
	boolean setScore(double score);
	boolean setOutOf(double outOf);
	void removeRecord();
}
