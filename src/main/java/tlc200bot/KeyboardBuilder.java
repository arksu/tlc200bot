package tlc200bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class KeyboardBuilder
{
	private List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
	private List<InlineKeyboardButton> lastRow;

	public static KeyboardBuilder start()
	{
		return new KeyboardBuilder().newRow();
	}

	public KeyboardBuilder newRow()
	{
		lastRow = new ArrayList<>();
		buttons.add(lastRow);
		return this;
	}

	public KeyboardBuilder addButton(String name, String data)
	{
		lastRow.add(new InlineKeyboardButton().setText(name).setCallbackData(data));
		return this;
	}

	public InlineKeyboardMarkup build()
	{
		InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
		markupKeyboard.setKeyboard(buttons);
		return markupKeyboard;
	}
}
