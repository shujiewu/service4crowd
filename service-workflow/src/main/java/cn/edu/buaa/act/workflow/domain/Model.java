package cn.edu.buaa.act.workflow.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author wsj
 */
@Entity
@Getter
@Setter
@Table(name="ACT_DE_MODEL")
public class Model extends AbstractModel {

	@Column(name="thumbnail")
	private byte[] thumbnail;
	
	public Model() {
		super();
	}

}