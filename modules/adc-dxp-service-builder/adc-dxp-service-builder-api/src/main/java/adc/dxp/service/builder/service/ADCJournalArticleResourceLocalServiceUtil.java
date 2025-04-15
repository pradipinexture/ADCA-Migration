/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package adc.dxp.service.builder.service;

import com.liferay.osgi.util.service.Snapshot;

/**
 * Provides the local service utility for ADCJournalArticleResource. This utility wraps
 * <code>adc.dxp.service.builder.service.impl.ADCJournalArticleResourceLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see ADCJournalArticleResourceLocalService
 * @generated
 */
public class ADCJournalArticleResourceLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>adc.dxp.service.builder.service.impl.ADCJournalArticleResourceLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static com.liferay.journal.model.JournalArticle
		getJournalArticleByGroupIdAndArticleId(long groupId, String articleID) {

		return getService().getJournalArticleByGroupIdAndArticleId(
			groupId, articleID);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static ADCJournalArticleResourceLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ADCJournalArticleResourceLocalService>
		_serviceSnapshot = new Snapshot<>(
			ADCJournalArticleResourceLocalServiceUtil.class,
			ADCJournalArticleResourceLocalService.class);

}