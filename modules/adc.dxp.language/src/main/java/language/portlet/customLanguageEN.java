package language.portlet;

import com.liferay.portal.kernel.language.UTF8Control;
import org.osgi.service.component.annotations.Component;

import java.util.Enumeration;
import java.util.ResourceBundle;

@Component(
        property = {
                "language.id=en_US"
        },
        service = ResourceBundle.class
)
public class customLanguageEN extends ResourceBundle{
    ResourceBundle bundle;

    public customLanguageEN() {
        this.bundle = ResourceBundle.getBundle("content.Language_en_US", UTF8Control.INSTANCE);
    }
    protected Object handleGetObject(String key) {
        return this.bundle.getObject(key);
    }
    public Enumeration<String> getKeys() {
        return this.bundle.getKeys();
    }
}
