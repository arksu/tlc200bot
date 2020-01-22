package tlc200bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class Utils
{
	private static final Logger _log = LoggerFactory.getLogger(Utils.class.getName());

	public static boolean isEmpty(String s)
	{
		return s == null || s.length() == 0;
	}

	public static boolean isMember(String status)
	{
		return "creator".equalsIgnoreCase(status)
		       || "administrator".equalsIgnoreCase(status)
		       || "member".equalsIgnoreCase(status);
	}

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
				.setText(text)
				.setReplyMarkup(new ReplyKeyboardRemove());
	}

	public static BotApiMethod error(String text, Update u)
	{
		return new SendMessage()
				.setChatId(u.getMessage().getChatId())
				.setText(text)
				.setReplyMarkup(new ReplyKeyboardRemove());
	}

	public static String send(BotApiMethod msg)
	{
		ObjectMapper mapper = new ObjectMapper();
		try
		{
			String jsonString = mapper.writeValueAsString(msg);

			System.out.println(jsonString);

			HttpClient client = HttpClientBuilder.create()
			                                     .build();
			HttpPost post = new HttpPost();
			post.setURI(URI.create("https://api.telegram.org/bot" + ServerConfig.TOKEN + "/" + msg.getMethod()));
			final StringEntity entity = new StringEntity(jsonString, StandardCharsets.UTF_8);
			entity.setContentType("application/json");
			post.setEntity(entity);

			final HttpResponse response = client.execute(post);

			if (response.getStatusLine().getStatusCode() != 200)
			{
				_log.warn(response.toString());
			}
			else
			{
				final String r = EntityUtils.toString(response.getEntity());
				_log.debug("result: " + r);
				return r;
			}
		}
		catch (JsonProcessingException | UnsupportedEncodingException e)
		{
			e.printStackTrace();

		}
		catch (ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static void send(PartialBotApiMethod msg, String method)
	{
		ObjectMapper mapper = new ObjectMapper();
		try
		{
			String jsonString = mapper.writeValueAsString(msg);

			System.out.println(jsonString);

			HttpClient client = HttpClientBuilder.create()
			                                     .build();
			HttpPost post = new HttpPost();
			post.setURI(URI.create("https://api.telegram.org/bot" + ServerConfig.TOKEN + "/" + method));
			final StringEntity entity = new StringEntity(jsonString, StandardCharsets.UTF_8);
			entity.setContentType("application/json");
			post.setEntity(entity);

			final HttpResponse response = client.execute(post);

			// TODO
			if (response.getStatusLine().getStatusCode() != 200)
			{
				System.out.println(response.toString());
			}

		}
		catch (JsonProcessingException | UnsupportedEncodingException e)
		{
			e.printStackTrace();

		}
		catch (ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
