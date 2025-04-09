package adc.dxp.rest.api.application.data.comparator;

import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * Comparator for sorting JournalArticles by title
 */
public class JournalArticleTitleComparator extends OrderByComparator<JournalArticle> {

	private static final long serialVersionUID = 1L;

	public static final String ORDER_BY_ASC = "JournalArticleLocalization.title ASC";
	public static final String ORDER_BY_DESC = "JournalArticleLocalization.title DESC";
	public static final String[] ORDER_BY_FIELDS = {"title"};

	public JournalArticleTitleComparator() {
		this(false);
	}

	public JournalArticleTitleComparator(boolean ascending) {
		_ascending = ascending;
	}

	@Override
	public int compare(JournalArticle article1, JournalArticle article2) {
		String title1 = article1.getTitle().toLowerCase();
		String title2 = article2.getTitle().toLowerCase();

		int value = title1.compareTo(title2);

		if (_ascending) {
			return value;
		} else {
			return -value;
		}
	}

	@Override
	public String getOrderBy() {
		if (_ascending) {
			return ORDER_BY_ASC;
		}
		return ORDER_BY_DESC;
	}

	@Override
	public String[] getOrderByFields() {
		return ORDER_BY_FIELDS;
	}

	@Override
	public boolean isAscending() {
		return _ascending;
	}

	private final boolean _ascending;
}