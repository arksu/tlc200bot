package tlc200bot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "profile")
public class Profile extends DbObject
{
	@Id
	@Column(name = "id", columnDefinition = "BIGINT NOT NULL")
	private long _id;

	@Column(name = "name", columnDefinition = "VARCHAR(96) NULL")
	private String _name;

	@Column(name = "nameMsgId", columnDefinition = "INT")
	private int _nameMsgId;

	@Column(name = "city", columnDefinition = "VARCHAR(96) NULL")
	private String _city;

	@Column(name = "cityMsgId", columnDefinition = "INT")
	private int _cityMsgId;

	@Column(name = "yearAuto", columnDefinition = "VARCHAR(96) NULL")
	private String _yearAuto;

	@Column(name = "yearAutoMsgId", columnDefinition = "INT")
	private int _yearAutoMsgId;

	public long getId()
	{
		return _id;
	}

	public void setId(long id)
	{
		_id = id;
	}

	public String getName()
	{
		return _name;
	}

	public void setName(String name)
	{
		_name = name;
	}

	public String getCity()
	{
		return _city;
	}

	public void setCity(String city)
	{
		_city = city;
	}

	public String getYearAuto()
	{
		return _yearAuto;
	}

	public void setYearAuto(String yearAuto)
	{
		_yearAuto = yearAuto;
	}

	public int getNameMsgId()
	{
		return _nameMsgId;
	}

	public void setNameMsgId(int nameMsgId)
	{
		_nameMsgId = nameMsgId;
	}

	public int getCityMsgId()
	{
		return _cityMsgId;
	}

	public void setCityMsgId(int cityMsgId)
	{
		_cityMsgId = cityMsgId;
	}

	public int getYearAutoMsgId()
	{
		return _yearAutoMsgId;
	}

	public void setYearAutoMsgId(int yearAutoMsgId)
	{
		_yearAutoMsgId = yearAutoMsgId;
	}
}
