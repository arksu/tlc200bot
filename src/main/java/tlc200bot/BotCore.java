package tlc200bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import tlc200bot.model.MarketPost;
import tlc200bot.model.User;

import static tlc200bot.model.UserState.*;

public class BotCore extends TelegramWebhookBot
{
	private static final Logger _log = LoggerFactory.getLogger(BotCore.class.getName());

	private static final long TEST_CHAT_ID = -364010507L;

	private static final String BARAHOLKA = "baraholka";
	private static final String BARAHOLKA_NEW = BARAHOLKA + ":new";
	private static final String BARAHOLKA_POST = BARAHOLKA + ":post";
	private static final String BARAHOLKA_TITLE = BARAHOLKA + ":title";
	private static final String BARAHOLKA_TEXT = BARAHOLKA + ":text";
	private static final String BARAHOLKA_PHOTO = BARAHOLKA + ":photo";
	private static final String BARAHOLKA_PHONE = BARAHOLKA + ":phone";
	private static final String BARAHOLKA_CANCEL = BARAHOLKA + ":cancel";

	/**
	 * запрос на участие в группе
	 */
	private static final String REQUEST = "request";
	private static final String REQUEST_NAME = REQUEST + ":name";

	/**
	 * заполнить анкету
	 */
	private static final String PROFILE = "profile";
	private static final String PROFILE_NEW = PROFILE + ":new";

	private static final String MAIN_MENU = "menu";
	private static final String FINISH = "finish";
	private static final String TEST = "test";

	private static final ObjectMapper mapper = new ObjectMapper();

	/**
	 * This method is called when receiving updates via webhook
	 * @param update Update received
	 */
	@Override
	public BotApiMethod onWebhookUpdateReceived(Update update)
	{
		_log.debug("onWebhookUpdateReceived ");
		try
		{
			_log.debug(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(update));
		}
		catch (JsonProcessingException e)
		{
			_log.error(e.getMessage(), e);
		}

		try
		{
			if (update.hasMessage())
			{
				final Message msg = update.getMessage();
				final Long chatId = msg.getChatId();
				final String text = msg.getText();
//				if (msg.hasText())
//				{
				_log.debug("message: from=" + msg.getFrom().getId() + "[" + msg.getFrom().getFirstName() + "-" + msg.getFrom().getUserName() + "] -> [" + chatId + "] " + text);

				if (chatId == TEST_CHAT_ID)
				{
				}
				else
				{
					return processPersonalChat(update);
				}
//				}
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
		}
		catch (Throwable e)
		{
			_log.error("error", e);
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
			user.setFirstName(from.getFirstName());
			user.setLastName(from.getLastName());
			user.setUserName(from.getUserName());
			user.persist();

			final int activeMarketPostId = user.getActiveMarketPost();
			if (user.getState() == WaitMarketPostTitle && msg.hasText())
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
			else if (user.getState() == WaitMarketPostText && msg.hasText())
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

					return makeBaraholkaMenu(marketPost, new SendMessage().setChatId(chatId));
				}
				else
				{
					user.setState(None);
					user.persist();

					return Utils.error("no active market post", update);
				}
			}
			else if (user.getState() == WaitMarketPostPhoto)
			{
				MarketPost marketPost = Database.em().findById(MarketPost.class, activeMarketPostId);
				if (marketPost == null)
				{
					return Utils.error("no market post " + activeMarketPostId, update);
				}
				if (msg.hasPhoto())
				{

					PhotoSize max = null;
					int sz = 0;
					for (PhotoSize size : msg.getPhoto())
					{
						if (size.getWidth() > sz)
						{
							max = size;
							sz = size.getWidth();
						}
					}
					if (max != null)
					{
						marketPost.setPhoto(msg.getMessageId());
						marketPost.persist();
					}
				}
				user.setState(None);
				user.persist();

				return makeBaraholkaMenu(marketPost, new SendMessage().setChatId(chatId));
			}
			else if (user.getState() == WaitMarketPostPhone)
			{
				MarketPost marketPost = Database.em().findById(MarketPost.class, activeMarketPostId);
				if (marketPost == null)
				{
					return Utils.error("no market post " + activeMarketPostId, update);
				}
				if (msg.hasContact())
				{

					String number = msg.getContact().getPhoneNumber();
					if (!Utils.isEmpty(number))
					{
						if (number.startsWith("7"))
						{
							number = "+" + number;
						}
						marketPost.setPhone(number);
						marketPost.persist();

						Utils.send(new SendMessage()
								           .setChatId(chatId)
								           .setText("Телефон будет указан в объявлении")
								           .setReplyMarkup(new ReplyKeyboardRemove()));
					}
				}
				else
				{
					Utils.send(new SendMessage()
							           .setChatId(chatId)
							           .setText("Я не получил от тебя номер телефона")
							           .setReplyMarkup(new ReplyKeyboardRemove()));
				}
				user.setState(None);
				user.persist();

				return makeBaraholkaMenu(marketPost, new SendMessage().setChatId(chatId));
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
			user.setPersonalChatId(msg.getChatId());

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
			else if (TEST.equals(data))
			{
				ForwardMessage m = new ForwardMessage();
				m.setChatId(TEST_CHAT_ID);
				m.setFromChatId(316558811L);
				m.setMessageId(605);

				Utils.send(m);
				return null;
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
					marketPost.setUserId(user.getId());
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
				                        .setText("OK. Отправь мне НАЗВАНИЕ твоего объявления.");

			}
			else if (BARAHOLKA_TEXT.equals(data))
			{
				user.setState(WaitMarketPostText);
				user.persist();

				return new SendMessage().setChatId(chatId)
				                        .setText("OK. Отправь мне ТЕКСТ твоего объявления.");
			}
			else if (BARAHOLKA_PHOTO.equals(data))
			{
				user.setState(WaitMarketPostPhoto);
				user.persist();

				return new SendMessage().setChatId(chatId)
				                        .setText("OK. Отправь мне ФОТО для твоего объявления.");
			}
			else if (BARAHOLKA_PHONE.equals(data))
			{
				user.setState(WaitMarketPostPhone);
				user.persist();

				return new SendMessage().setChatId(chatId)
				                        .setText("OK. Отправь мне ТЕЛЕФОН для твоего объявления.")
				                        .setReplyMarkup(
						                        KeyboardReplyBuilder.start()
						                                            .addButtonPhone("Отправить номер")
						                                            .build()

				                        );
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

						SendMessage m = new SendMessage();
						m.setChatId(TEST_CHAT_ID);
						m.setText(
								"Новое объявление от " + user.getVisible() + "\r\n" +
								marketPost.getTitle() + "\r\n" +
								marketPost.getText() +
								(Utils.isEmpty(marketPost.getPhone()) ? "" : "\r\nТелефон: " + marketPost.getPhone())
						);
						Utils.send(m);

						if (marketPost.getPhoto() > 0)
						{
							ForwardMessage f = new ForwardMessage();
							f.setChatId(TEST_CHAT_ID);
							f.setFromChatId(user.getPersonalChatId());
							f.setMessageId(((int) marketPost.getPhoto()));

							Utils.send(f);
						}

						user.setActiveMarketPost(0);
						user.persist();

						Utils.send(Utils.deleteMessage(cb));
						Utils.send(new SendMessage()
								           .setChatId(chatId)
								           .setText("Ваше объявление опубликовано!"));
						return makeMainMenu(new SendMessage().setChatId(chatId));
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
			text = "Название объявления: " + marketPost.getTitle() + "\r\n" +
			       "Текст объявления: " + marketPost.getText();
			if (!Utils.isEmpty(marketPost.getPhone()))
			{
				text += "\r\nТелефон: " + marketPost.getPhone();
			}
		}
		if (m instanceof SendMessage)
		{
			return ((SendMessage) m)
					.setText(text)
					.setReplyMarkup(KeyboardBuilder
							                .start()
							                .addButton("Название", BARAHOLKA_TITLE)
							                .addButton("Текст", BARAHOLKA_TEXT)
							                .newRow()
							                .addButton("Добавить фото", BARAHOLKA_PHOTO)
							                .addButton("Указать телефон", BARAHOLKA_PHONE)
							                .newRow()
							                .addButton("Отменить", BARAHOLKA_CANCEL)
							                .addButton("Опубликовать", BARAHOLKA_POST)
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
							                .addButton("Название", BARAHOLKA_TITLE)
							                .addButton("Текст", BARAHOLKA_TEXT)
							                .newRow()
							                .addButton("Добавить фото", BARAHOLKA_PHOTO)
							                .addButton("Указать телефон", BARAHOLKA_PHONE)
							                .newRow()
							                .addButton("Отменить", BARAHOLKA_CANCEL)
							                .addButton("Опубликовать", BARAHOLKA_POST)
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
									.addButton("Заполнить анкету", PROFILE_NEW)
									.newRow()
									.addButton("Объявление в барахолку", BARAHOLKA_NEW)
									.newRow()
									.addButton("Завершить работу", FINISH)
									.addButton("TEST", TEST)
									.build());
		}
		else if (m instanceof EditMessageText)
		{
			return ((EditMessageText) m)
					.setText("Меню")
					.setReplyMarkup(
							KeyboardBuilder
									.start()
									.addButton("Заполнить анкету", PROFILE_NEW)
									.newRow()
									.addButton("Объявление в барахолку", BARAHOLKA_NEW)
									.newRow()
									.addButton("Завершить работу", FINISH)
									.addButton("TEST", TEST)
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
