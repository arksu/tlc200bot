package tlc200bot;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Utils
{
	public static EditMessageText editMessageText(CallbackQuery cb)
	{
		return new EditMessageText()
				.setChatId(cb.getMessage().getChatId())
				.setMessageId(cb.getMessage().getMessageId())
				.setInlineMessageId(cb.getInlineMessageId());
	}

	public static DeleteMessage deleteMessage(CallbackQuery cb)
	{
		return new DeleteMessage()
				.setChatId(cb.getMessage().getChatId())
				.setMessageId(cb.getMessage().getMessageId());
	}

	public static BotApiMethod error(String text, CallbackQuery cb)
	{
		return new SendMessage()
				.setChatId(cb.getMessage().getChatId())
				.setText(text);
	}

	public static BotApiMethod error(String text, Update u)
	{
		return new SendMessage()
				.setChatId(u.getMessage().getChatId())
				.setText(text);
	}
}
