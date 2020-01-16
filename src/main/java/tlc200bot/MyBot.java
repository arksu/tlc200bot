package tlc200bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

public class MyBot extends TelegramWebhookBot
{
	private static final Logger _log = LoggerFactory.getLogger(MyBot.class.getName());

	private static final long TEST_CHAT_ID = -364010507L;

	private static final String BARAHOLKA = "baraholka";
	private static final String BARAHOLKA_POST = BARAHOLKA + ":post";
	private static final String BARAHOLKA_CAPTION = BARAHOLKA + ":caption";
	private static final String BARAHOLKA_TEXT = BARAHOLKA + ":text";
	private static final String BARAHOLKA_PHOTO = BARAHOLKA + ":photo";

	private static final String FINISH = "finish";

	/**
	 * This method is called when receiving updates via webhook
	 * @param update Update received
	 */
	@Override
	public BotApiMethod onWebhookUpdateReceived(Update update)
	{
		_log.debug("onWebhookUpdateReceived ");

		if (update.hasMessage())
		{
			final Message msg = update.getMessage();
			final Long chatId = msg.getChatId();
			final String text = msg.getText();
			if (msg.hasText())
			{
				_log.debug("message: from=" + msg.getFrom().getId() + "[" + msg.getFrom().getFirstName() + "-" + msg.getFrom().getUserName() + "] -> [" + chatId + "] " + text);

				if (chatId == TEST_CHAT_ID)
				{
				}
				else
				{
					return new SendMessage()
							.setChatId(chatId)
							.setText("Меню")
							.setReplyMarkup(
									KeyboardBuilder
											.start()
											.addButton("Пост в барахолку", BARAHOLKA_POST)
											.newRow()
											.addButton("Завершить", FINISH)
											.build());
				}
			}
		}
		else if (update.hasCallbackQuery())
		{
			final CallbackQuery callbackQuery = update.getCallbackQuery();

			_log.debug("callbackQuery");
			_log.debug(update.toString());

			return processCallbackQuery(callbackQuery);
		}
		else if (update.hasInlineQuery())
		{
			InlineQuery q = update.getInlineQuery();
			_log.debug("InlineQuery " + q.getFrom().getId() + "[" + q.getFrom().getUserName() + "] " + q.getQuery());
//			return new AnswerInlineQuery()
//					.setPersonal(true)
//					.setInlineQueryId(q.getId())
//					.setResults(new InlineQueryResultArticle()
//							            .setId("ldkjgletjll5439543543kn534kjcvxx467khj")
//							            .setTitle("some t")
//							            .setDescription("some desc")
//							            .setDescription("some")
//							            .setUrl("https://google.com")
//							            .setInputMessageContent(new InputTextMessageContent()
//									                                    .setMessageText("some text")
//							            )
//					);
		}
		else
		{
			_log.debug(update.toString());
		}

		return null;
	}

	private BotApiMethod processCallbackQuery(CallbackQuery cb)
	{
		final String data = cb.getData();

		_log.debug("callbackQuery process");
		_log.debug(cb.getFrom().getId() + "[" + cb.getFrom().getFirstName() + "-" + cb.getFrom().getUserName() + "]: " + data);

		if (FINISH.equals(data))
		{
			return Utils.deleteMessage(cb);
		}
		else if (BARAHOLKA_POST.equals(data))
		{
			return Utils.editMessageText(cb)
			            .setText("Пост в барахолку")
			            .setReplyMarkup(KeyboardBuilder
					                            .start()
					                            .addButton("Задать тему", BARAHOLKA_CAPTION)
					                            .addButton("Задать текст", BARAHOLKA_TEXT)
					                            .addButton("Добавить фото", BARAHOLKA_PHOTO)
					                            .newRow()
					                            .addButton("Завершить", FINISH)
					                            .build());
		}
		else if (BARAHOLKA_TEXT.equals(data))
		{
			return new SendMessage()
					.setChatId(TEST_CHAT_ID)
					.setText("Тестовый пост мол в барахолку из бота");
		}
		else
		{
			return Utils.deleteMessage(cb);
		}
	}

	/**
	 * Gets bot username of this bot
	 * @return Bot username
	 */
	@Override
	public String getBotUsername()
	{
		return "TLC200";
	}

	/**
	 * Returns the token of the bot to be able to perform Telegram Api Requests
	 * @return Token of the bot
	 */
	@Override
	public String getBotToken()
	{
		return "919280692:AAGKL3r4IkSdfAmZgjAI-vtSLyxA3zCMlgA";
	}

	/**
	 * Gets in the url for the webhook
	 * @return path in the url
	 */
	@Override
	public String getBotPath()
	{
		return "t1";
	}
}
