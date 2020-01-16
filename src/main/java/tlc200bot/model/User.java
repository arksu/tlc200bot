package tlc200bot.model;

import org.jpark.TableExtended;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "users")
@TableExtended(drop = true)
public class User
{
	@Column(name = "id", columnDefinition = "VARCHAR(32) NOT NULL")
	private String _id;
}
