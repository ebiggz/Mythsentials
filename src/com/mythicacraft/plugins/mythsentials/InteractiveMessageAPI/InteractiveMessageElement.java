package com.mythicacraft.plugins.mythsentials.InteractiveMessageAPI;


public class InteractiveMessageElement {

	private FormattedText text;
	private HoverEvent hoverEventType;
	private FormattedText hoverText;
	private ClickEvent clickEventType;
	private String command;


	public InteractiveMessageElement(String text) {
		this(text, HoverEvent.NONE, null, ClickEvent.NONE, null);
	}

	public InteractiveMessageElement(FormattedText text) {
		this(text, HoverEvent.NONE, null, ClickEvent.NONE, null);
	}

	public InteractiveMessageElement(String textString, HoverEvent hoverEventType, String hoverTextString, ClickEvent clickEventType, String command) {
		this(new FormattedText(textString), hoverEventType, new FormattedText(hoverTextString), clickEventType, command);
	}

	public InteractiveMessageElement(FormattedText text, HoverEvent hoverEventType, FormattedText hoverText, ClickEvent clickEventType, String command) {
		this.text = text;
		this.hoverEventType = hoverEventType;
		this.hoverText = hoverText;
		this.clickEventType = clickEventType;
		this.command = command;
	}


	public enum HoverEvent {
		SHOW_TEXT, NONE
	}

	public enum ClickEvent {
		SUGGEST_COMMAND, RUN_COMMAND, NONE
	}

	public FormattedText getMainText() {
		return text;
	}

	public HoverEvent getHoverEventType() {
		return hoverEventType;
	}

	public boolean hasHoverEvent() {
		return hoverEventType != HoverEvent.NONE;
	}

	public FormattedText getHoverText() {
		return hoverText;
	}

	public ClickEvent getClickEventType() {
		return clickEventType;
	}

	public boolean hasClickEvent() {
		return clickEventType != ClickEvent.NONE;
	}

	public String getCommand() {
		return command;
	}
}
