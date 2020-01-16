package tlc200bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import tlc200bot.model.MarketPost;
import tlc200bot.model.User;

import static tlc200bot.model.UserState.*;

public class MyBot extends TelegramWebhookBot
{
	private static final Logger _log = LoggerFactory.getLogger(MyBot.class.getName());

	private static final long TEST_CHAT_ID = -364010507L;

	private static final String BARAHOLKA = "baraholka";
	private static final String BARAHOLKA_NEW = BARAHOLKA + ":new";
	private static final String BARAHOLKA_POST = BARAHOLKA + ":post";
	private static final String BARAHOLKA_TITLE = BARAHOLKA + ":title";
	private static final String BARAHOLKA_TEXT = BARAHOLKA + ":text";
	private static final String BARAHOLKA_PHOTO = BARAHOLKA + ":photo";
	private static final String BARAHOLKA_CANCEL = BARAHOLKA + ":cancel";

	private static final String MAIN_MENU = "menu";
	private static final String FINISH = "finish";

	/**
	 * This method is called when receiving updates via webhook
	 * @param update Update received
	 */
	@Override
	public BotApiMethod onWebhookUpdateReceived(Update update)
	{
		_log.debug("onWebhookUpdateReceived ");
		_log.debug(update.toString());

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
					return processPersonalChat(update);
				}
			}
		}
		else if (update.hasCallbackQuery())
		{
			final CallbackQuery callbackQuery = update.getCallbackQuery();

			_log.debug("callbackQuery");

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

	private BotApiMethod processPersonalChat(Update update)
	{
		final Message msg = update.getMessage();
		final Long chatId = msg.getChatId();
		final String text = msg.getText();

		final org.telegram.telegrambots.meta.api.objects.User from = msg.getFrom();
		User user = Database.em().findById(User.class, from.getId());

		// если юзер найден в базе
		if (user != null)
		{
			final int activeMarketPostId = user.getActiveMarketPost();
			if (user.getState() == WaitMarketPostTitle)
			{
				if (activeMarketPostId != 0)
				{
					MarketPost marketPost = Database.em().findById(MarketPost.class, activeMarketPostId);
					if (marketPost == null)
					{
						return Utils.error("no market post " + activeMarketPostId, update);
					}
					marketPost.setTitle(text);
					marketPost.persist();

					user.setState(None);
					user.persist();

					return makeBaraholkaMenu(marketPost, new SendMessage().setChatId(chatId));
				}
				else
				{
					user.setState(None);
					user.persist();

					return Utils.error("no active market post", update);
				}
			}
			else if (user.getState() == WaitMarketPostText)
			{
				if (activeMarketPostId != 0)
				{
					MarketPost marketPost = Database.em().findById(MarketPost.class, activeMarketPostId);
					if (marketPost == null)
					{
						return Utils.error("no market post " + activeMarketPostId, update);
					}
					marketPost.setText(text);
					marketPost.persist();

					user.setState(None);
					user.persist();

					return makeBaraholkaMenu(marketPost, new SendMessage().setChatId(chatId)
					);
				}
				else
				{
					user.setState(None);
					user.persist();

					return Utils.error("no active market post", update);
				}
			}
		}
		else
		{
			// юзера еще нет - создадим его
			user = new User();
			user.setId(from.getId());
			user.setFirstName(from.getFirstName());
			user.setLastName(from.getLastName());
			user.setUserName(from.getUserName());

			Database.em().persist(user);
		}

		return makeMainMenu(new SendMessage().setChatId(chatId));
	}

	private BotApiMethod processCallbackQuery(CallbackQuery cb)
	{
		// ищем такого юзера в базе
		User user = Database.em().findById(User.class, cb.getFrom().getId());

		// если не нашли - не восстановим состояние
		if (user == null)
		{
			// поэтому просто грохнем сообщение из которого сюда попали
			return Utils.deleteMessage(cb);
		}
		else
		{

			final String data = cb.getData();
			final Long chatId = cb.getMessage().getChatId();

			_log.debug("callbackQuery process");
			_log.debug(cb.getFrom().getId() + "[" + cb.getFrom().getFirstName() + "-" + cb.getFrom().getUserName() + "]: " + data);

			if (FINISH.equals(data))
			{
				return Utils.deleteMessage(cb);
			}
			else if (MAIN_MENU.equals(data))
			{
				return makeMainMenu(Utils.editMessageText(cb));
			}
			else if (BARAHOLKA_NEW.equals(data))
			{
				final int activeMarketPostId = user.getActiveMarketPost();
				// есть активное не завершенное объявление?
				if (activeMarketPostId != 0)
				{
					MarketPost marketPost = Database.em().findById(MarketPost.class, activeMarketPostId);
					if (marketPost == null)
					{
						return Utils.error("no market post " + activeMarketPostId, cb);
					}

					return makeBaraholkaMenu(marketPost, Utils.editMessageText(cb));
				}
				else
				{
					// объявления нет. создаем новое
					MarketPost marketPost = new MarketPost();
					marketPost.persist();

					user.setActiveMarketPost(marketPost.getId());
					user.persist();

					return makeBaraholkaMenu(null, Utils.editMessageText(cb));
				}
			}
			else if (BARAHOLKA_TITLE.equals(data))
			{
				user.setState(WaitMarketPostTitle);
				user.persist();

				return new SendMessage().setChatId(chatId)
				                        .setText("OK. Отправь мне ТЕМУ твоего объявления.");

			}
			else if (BARAHOLKA_TEXT.equals(data))
			{
				user.setState(WaitMarketPostText);
				user.persist();

				return new SendMessage().setChatId(chatId)
				                        .setText("OK. Отправь мне ТЕКСТ твоего объявления.");
			}
			else if (BARAHOLKA_CANCEL.equals(data))
			{
				final int activeMarketPostId = user.getActiveMarketPost();
				// есть активное не завершенное объявление?
				if (activeMarketPostId != 0)
				{
					user.setActiveMarketPost(0);
					user.persist();
				}
				return makeMainMenu(Utils.editMessageText(cb));
			}
			else if (BARAHOLKA_POST.equals(data))
			{
				final int activeMarketPostId = user.getActiveMarketPost();
				// есть активное не завершенное объявление?
				if (activeMarketPostId != 0)
				{
					MarketPost marketPost = Database.em().findById(MarketPost.class, activeMarketPostId);
					if (marketPost == null)
					{
						return Utils.error("no market post " + activeMarketPostId, cb);
					}

					if (marketPost.getTitle().length() > 0 && marketPost.getText().length() > 0)
					{
						// TODO публикуем!
						_log.debug("POST!");

						user.setActiveMarketPost(0);
						user.persist();
					}
					else
					{
						return null;
					}
				}
				return makeMainMenu(Utils.editMessageText(cb));
			}
			else
			{
				return Utils.deleteMessage(cb);
			}
		}
	}

	private BotApiMethod makeBaraholkaMenu(MarketPost marketPost, BotApiMethod m)
	{
		String text;
		if (marketPost == null)
		{
			text = "Новое объявление в барахолку";
		}
		else
		{
			text = "Объявление в барахолку\r\n" +
			       "Тема: " + marketPost.getTitle() + "\r\n" +
			       "Текст: " + marketPost.getText();
		}
		if (m instanceof SendMessage)
		{
			return ((SendMessage) m)
					.setText(text)
					.setReplyMarkup(KeyboardBuilder
							                .start()
							                .addButton("Задать тему", BARAHOLKA_TITLE)
							                .addButton("Задать текст", BARAHOLKA_TEXT)
							                .addButton("Добавить фото", BARAHOLKA_PHOTO)
							                .newRow()
							                .addButton("Отменить создание поста", BARAHOLKA_CANCEL)
							                .addButton("Опубликовать объявление", BARAHOLKA_POST)
							                .newRow()
							                .addButton("< Обратно в меню", MAIN_MENU)
							                .build());
		}
		else if (m instanceof EditMessageText)
		{
			return ((EditMessageText) m)
					.setText(text)
					.setReplyMarkup(KeyboardBuilder
							                .start()
							                .addButton("Задать тему", BARAHOLKA_TITLE)
							                .addButton("Задать текст", BARAHOLKA_TEXT)
							                .addButton("Добавить фото", BARAHOLKA_PHOTO)
							                .newRow()
							                .addButton("Отменить создание поста", BARAHOLKA_CANCEL)
							                .addButton("Опубликовать объявление", BARAHOLKA_POST)
							                .newRow()
							                .addButton("< Обратно в меню", MAIN_MENU)
							                .build());

		}
		return m;
	}

	private BotApiMethod makeMainMenu(BotApiMethod m)
	{
		if (m instanceof SendMessage)
		{
			return ((SendMessage) m)
					.setText("Меню")
					.setReplyMarkup(
							KeyboardBuilder
									.start()
									.addButton("Пост в барахолку", BARAHOLKA_NEW)
									.newRow()
									.addButton("Завершить работу", FINISH)
									.build());
		}
		else if (m instanceof EditMessageText)
		{
			return ((EditMessageText) m)
					.setText("Меню")
					.setReplyMarkup(
							KeyboardBuilder
									.start()
									.addButton("Пост в барахолку", BARAHOLKA_NEW)
									.newRow()
									.addButton("Завершить работу", FINISH)
									.build());
		}
		return m;
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
		return ServerConfig.TOKEN;
	}

	/**
	 * Gets in the url for the webhook
	 * @return path in the url
	 */
	@Override
	public String getBotPath()
	{
		return ServerConfig.PATH;
	}
}
