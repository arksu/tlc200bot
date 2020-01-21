package tlc200bot.model;

import org.jpark.TableExtended;
import tlc200bot.Database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "users")
@TableExtended(drop = false)
public class User
{
	@Id
	@Column(name = "id", columnDefinition = "long NOT NULL")
	private long _id;

	@Column(name = "userName", columnDefinition = "VARCHAR(32) NULL")
	private String _userName;

	@Column(name = "firstName", columnDefinition = "VARCHAR(32) NULL")
	private String _firstName;

	@Column(name = "lastName", columnDefinition = "VARCHAR(32) NULL")
	private String _lastName;

	@Column(name = "activeMarketPost", columnDefinition = "INT(11) DEFAULT 0")
	private int _activeMarketPost;

	@Column(name = "state", columnDefinition = "INT(11) NULL")
	private int _state;

	@Column(name = "createTime", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
	private Timestamp _createTime;

	public long getId()
	{
		return _id;
	}

	public void setId(long id)
	{
		_id = id;
	}

	public String getFirstName()
	{
		return _firstName;
	}

	public void setFirstName(String firstName)
	{
		_firstName = firstName;
	}

	public String getLastName()
	{
		return _lastName;
	}

	public void setLastName(String lastName)
	{
		_lastName = lastName;
	}

	public String getUserName()
	{
		return _userName;
	}

	public void setUserName(String userName)
	{
		_userName = userName;
	}

	public int getActiveMarketPost()
	{
		return _activeMarketPost;
	}

	public void setActiveMarketPost(int activeMarketPost)
	{
		_activeMarketPost = activeMarketPost;
	}

	public UserState getState()
	{
		return UserState.get(_state);
	}

	public void setState(int state)
	{
		_state = state;
	}

	public void setState(UserState state)
	{
		_state = state.id;
	}

	public Timestamp getCreateTime()
	{
		return _createTime;
	}

	public void persist()
	{
		Database.em().persist(this);
	}
}
