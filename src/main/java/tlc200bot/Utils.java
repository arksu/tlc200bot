package tlc200bot;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

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
}
