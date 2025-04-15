/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package adc.dxp.service.builder.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link ADCJournalArticleResourceLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ADCJournalArticleResourceLocalService
 * @generated
 */
public class ADCJournalArticleResourceLocalServiceWrapper
	implements ADCJournalArticleResourceLocalService,
			   ServiceWrapper<ADCJournalArticleResourceLocalService> {

	public ADCJournalArticleResourceLocalServiceWrapper() {
		this(null);
	}

	public ADCJournalArticleResourceLocalServiceWrapper(
		ADCJournalArticleResourceLocalService
			adcJournalArticleResourceLocalService) {

		_adcJournalArticleResourceLocalService =
			adcJournalArticleResourceLocalService;
	}

	@Override
	public com.liferay.journal.model.JournalArticle
		getJournalArticleByGroupIdAndArticleId(long groupId, String articleID) {

		return _adcJournalArticleResourceLocalService.
			getJournalArticleByGroupIdAndArticleId(groupId, articleID);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _adcJournalArticleResourceLocalService.
			getOSGiServiceIdentifier();
	}

	@Override
	public ADCJournalArticleResourceLocalService getWrappedService() {
		return _adcJournalArticleResourceLocalService;
	}

	@Override
	public void setWrappedService(
		ADCJournalArticleResourceLocalService
			adcJournalArticleResourceLocalService) {

		_adcJournalArticleResourceLocalService =
			adcJournalArticleResourceLocalService;
	}

	private ADCJournalArticleResourceLocalService
		_adcJournalArticleResourceLocalService;

}