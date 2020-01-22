package tlc200bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardReplyBuilder
{
	private List<KeyboardRow> rows = new ArrayList<>();
	private KeyboardRow lastRow;

	public static KeyboardReplyBuilder start()
	{
		return new KeyboardReplyBuilder().newRow();
	}

	public KeyboardReplyBuilder newRow()
	{
		lastRow = new KeyboardRow();
		rows.add(lastRow);
		return this;
	}

	public KeyboardReplyBuilder addButton(String name)
	{
		lastRow.add(new KeyboardButton().setText(name));
		return this;
	}

	public KeyboardReplyBuilder addButtonPhone(String name)
	{
		lastRow.add(new KeyboardButton()
				            .setText(name)
				            .setRequestContact(true)
		);
		return this;
	}

	public ReplyKeyboardMarkup build()
	{
		ReplyKeyboardMarkup markupKeyboard = new ReplyKeyboardMarkup();
		markupKeyboard.setKeyboard(rows);
		return markupKeyboard;
	}

}
