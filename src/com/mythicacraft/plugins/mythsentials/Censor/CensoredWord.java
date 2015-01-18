package com.mythicacraft.plugins.mythsentials.Censor;


public class CensoredWord {

	private String word;
	private String replacement;
	private boolean wordBoundry;
	private boolean exact;
	private String regexPattern;

	public CensoredWord(String word, String replacement, boolean wordBoundry) {
		this.setWord(word);
		this.setReplacement(replacement);
		this.useWordBoundry(wordBoundry);
		exact = false;
		createPattern();
	}

	public CensoredWord(String word, String replacement, boolean wordBoundry, boolean exact) {
		this.setWord(word);
		this.setReplacement(replacement);
		this.useWordBoundry(wordBoundry);
		this.exact = exact;
		createPattern();
	}

	private void createPattern() {
		StringBuilder sb = new StringBuilder();
		String badWordString = this.getWord();
		String[] chars = badWordString.split("|");
		if(this.hasWordBoundry()) {
			sb.append("\\b");
		}
		String previous = "";
		for(String letter : chars) {
			if(letter.isEmpty()) continue;
			if(previous.equalsIgnoreCase(letter) && exact == false) continue;
			sb.append("[" + letter.toLowerCase() + letter.toUpperCase());
			if(letter.equalsIgnoreCase("s")) {
				sb.append("\\" + "\\$");
				sb.append("5");
			}
			else if(letter.equalsIgnoreCase("i")) {
				sb.append("\\" + "\\!");
				sb.append("1");
			}
			else if(letter.equalsIgnoreCase("t")) {
				sb.append("7");
			}
			else if(letter.equalsIgnoreCase("o")) {
				sb.append("0");
			}
			sb.append("]");
			if(!exact) {
				sb.append("+");
			}
			if(letter != chars[chars.length-1] && !exact) {
				sb.append("\\S?");
			}
			previous = letter;
		}
		if(this.hasWordBoundry()) {
			sb.append("\\b");
		}
		regexPattern = sb.toString();
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getReplacement() {
		return replacement;
	}

	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}

	public boolean isExact() {
		return exact;
	}

	public void setExact(boolean exact) {
		this.exact = exact;
	}

	public boolean hasWordBoundry() {
		return wordBoundry;
	}

	public void useWordBoundry(boolean wordBoundry) {
		this.wordBoundry = wordBoundry;
	}

	public String getRegexPattern() {
		return regexPattern;
	}
}
