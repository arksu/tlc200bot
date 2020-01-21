package tlc200bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

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

	public static void send(BotApiMethod msg)
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
