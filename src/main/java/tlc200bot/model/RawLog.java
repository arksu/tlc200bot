package tlc200bot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "rawlog")
public class RawLog extends DbObject
{
	@Id
	@Column(name = "id", columnDefinition = "INT NOT NULL AUTO_INCREMENT")
	private long _id;

	@Column(name = "text", columnDefinition = "TEXT")
	private String _text;

	@Column(name = "date", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
	private Timestamp _date;

	public String getText()
	{
		return _text;
	}

	public void setText(String text)
	{
		_text = text;
	}

	public Timestamp getDate()
	{
		return _date;
	}
}
