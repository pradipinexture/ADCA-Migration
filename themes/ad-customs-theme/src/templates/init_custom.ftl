<#--
This file allows you to override and define new FreeMarker variables.
-->

<#assign locale = theme_display.getLanguageId()
happylbl= 'Happy'
sadlbl = 'Sad'
neutrallbl = 'Neutral'
thankslbl ='Thank you!'
submittedlbl='Your feedback has been submitted '
surveySatisfactionQuestion = 'Are you satisfied the page content?'
surveyHeader= 'Satisfaction Survey'
surveyFeedback ='Feedback'
/>

<#if locale != 'en_US'>

<#assign 
happylbl= 'راض'
sadlbl = 'مستاء'
neutrallbl = 'محايد'
thankslbl ='!شكرا لكم'
submittedlbl='تم ارسال تعليقاتكم'
surveySatisfactionQuestion = 'هل أنت راض عن محتوى الصفحة؟'
surveyHeader= 'استبيان الرضا'
surveyFeedback ='التعليق'
/>

</#if>