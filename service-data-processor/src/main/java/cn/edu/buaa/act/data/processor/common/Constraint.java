package cn.edu.buaa.act.data.processor.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wsj
 */
@Getter
@Setter
public class Constraint {

	public static final String OPERATOR_EQUAL = "==";
	public static final String OPERATOR_CONTAINS = "CONTAINS";
	public static final String OPERATOR_NOT_EQUAL = "!=";
	public static final String OPERATOR_GREATER_THAN = ">";
	public static final String OPERATOR_LESS_THAN = "<";
	public static final String OPERATOR_GREATER_THAN_OR_EQUAL = ">=";
	public static final String OPERATOR_LESS_THAN_OR_EQUAL = "<=";

	private static final Pattern PARSE_REGEX = Pattern.compile("(?<field>[^ ]+) (?<op>[^ ]+)( (?<param>.+))?");

	private final String field;

	private final String operator;

	private final String parameter;

	public Constraint(String raw) {
        Matcher matcher = PARSE_REGEX.matcher(raw);
		Assert.isTrue(matcher.matches(), "Could not parse [" + raw + "] as a Marathon constraint (field operator param?)");
		this.field = matcher.group("field");
		this.operator = matcher.group("op");
		this.parameter = matcher.group("param"); // may be null
	}

	public List<String> toStringList() {
		if (this.parameter != null) {
			return Arrays.asList(this.field, this.operator, this.parameter);
		}
		else {
			return Arrays.asList(this.field, this.operator);
		}
	}

	@Override
	public String toString() {
		return this.field+" "+this.operator+" " +this.parameter;
	}
}