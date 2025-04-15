/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package adc.dxp.service.builder.service.impl;

import adc.dxp.service.builder.service.base.ADCJournalArticleResourceLocalServiceBaseImpl;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.aop.AopService;

import com.liferay.portal.kernel.exception.PortalException;
import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=adc.dxp.service.builder.model.ADCJournalArticleResource",
	service = AopService.class
)
public class ADCJournalArticleResourceLocalServiceImpl
	extends ADCJournalArticleResourceLocalServiceBaseImpl {

	public ADCJournalArticleResourceLocalServiceImpl() {
	}

	public JournalArticle getJournalArticleByGroupIdAndArticleId(long groupId, String articleID) {
		try {
			return JournalArticleLocalServiceUtil.getArticle(groupId, articleID);
		} catch (PortalException var5) {
			return null;
		}
	}
}