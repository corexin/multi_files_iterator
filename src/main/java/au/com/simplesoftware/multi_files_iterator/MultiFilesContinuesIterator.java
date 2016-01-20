package au.com.simplesoftware.multi_files_iterator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

/**
 * Iterate through all files passed in. read line by line or lines/block after
 * lines/block.
 * 
 * @author chao jiang
 *
 */
public class MultiFilesContinuesIterator {
	List<File> files = new ArrayList<File>();
	int linesInBlock = 1;
	int currentFileIndex = 0;
	int linesReadInAFile = 0;

	LineIterator currentFileLineIt = null;
	List<String> tempBlockLines = new ArrayList<String>();

	/**
	 * 
	 * @param fs
	 * @param linesInBlock
	 *            - how many lines read once?
	 */
	public MultiFilesContinuesIterator(List<File> fs, int linesInBlock) {
		this.files = fs;
		this.linesInBlock = linesInBlock;
	}

	public LinesBlock readNextBlock() throws IOException {
		if (currentFileIndex >= files.size()) {
			return null;
		}

		if (currentFileLineIt == null) {
			currentFileLineIt = FileUtils.lineIterator(files.get(currentFileIndex));
		}
		for (int i = linesReadInAFile; i < linesInBlock; i++) {
			if (currentFileLineIt.hasNext()) {
				tempBlockLines.add(currentFileLineIt.next());
				linesReadInAFile++;
			}
		}
		if (linesReadInAFile < linesInBlock) {
			currentFileIndex++;
			LineIterator.closeQuietly(currentFileLineIt);
			currentFileLineIt = null;
			return readNextBlock();
		} else if (linesReadInAFile == linesInBlock) {
			linesReadInAFile = 0;
			LinesBlock linesBlock = new LinesBlock(tempBlockLines);
			tempBlockLines.clear();
			return linesBlock;
		}

		return null;
	}

	public void closeAll() {
		LineIterator.closeQuietly(currentFileLineIt);
	}

	public static class LinesBlock {
		List<String> candles = new ArrayList<String>();

		public LinesBlock(List<String> ls) {
			for (String l : ls) {
				if (l != null) {
					candles.add(l);
				}
			}
		}

		@Override
		public String toString() {
			return "LinesBlock [lines=" + candles + "]";
		}

	}
}
