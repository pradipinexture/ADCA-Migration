package language.portlet;

import com.liferay.portal.kernel.language.UTF8Control;
import org.osgi.service.component.annotations.Component;

import java.util.Enumeration;
import java.util.ResourceBundle;

@Component(
        property = {
                "language.id=ar_SA"
        },
        service = ResourceBundle.class
)
public class CustomLanguageSA extends ResourceBundle{
    ResourceBundle bundle;

    public CustomLanguageSA() {
        this.bundle = ResourceBundle.getBundle("content.Language_ar_SA", UTF8Control.INSTANCE);
    }
    protected Object handleGetObject(String key) {
        return this.bundle.getObject(key);
    }
    public Enumeration<String> getKeys() {
        return this.bundle.getKeys();
    }
}
