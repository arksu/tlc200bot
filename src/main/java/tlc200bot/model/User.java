package tlc200bot.model;

import org.jpark.TableExtended;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "users")
@TableExtended(drop = true)
public class User extends DbObject
{
	@Id
	@Column(name = "id", columnDefinition = "BIGINT NOT NULL")
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

	/**
	 * ид чата лички с ботом
	 */
	@Column(name = "personalChatId", columnDefinition = "BIGINT NULL")
	private long _personalChatId;

	/**
	 * когда юзер был добавлен в базу (первое общение с ботом)
	 */
	@Column(name = "createTime", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
	private Timestamp _createTime;

	/**
	 * время последней проверки членства в группе
	 */
	@Column(name = "lastMembershipCheck", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
	private Timestamp _lastMembershipCheck;

	/**
	 * является участником группы / групп? действительный член сообщества?
	 */
	@Column(name = "isMember")
	private boolean _isMember;

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

	public String getVisible()
	{
		if (_userName != null)
		{
			return "@" + _userName;
		}
		return _firstName;
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

	public long getPersonalChatId()
	{
		return _personalChatId;
	}

	public void setPersonalChatId(long personalChatId)
	{
		_personalChatId = personalChatId;
	}

	public Timestamp getLastMembershipCheck()
	{
		return _lastMembershipCheck;
	}

	public void setLastMembershipCheck(Timestamp lastMembershipCheck)
	{
		_lastMembershipCheck = lastMembershipCheck;
	}

	public boolean isMember()
	{
		return _isMember;
	}

	public void setMember(boolean member)
	{
		_isMember = member;
	}

	public Timestamp getCreateTime()
	{
		return _createTime;
	}

	@Override
	public String toString()
	{
		return "(id=" + _id + " " + getVisible() + ")";
	}
}
