package jerozgen.languagereload.access;

public interface IClientLanguage {
    String languagereload_get(String key);

    String languagereload_getTargetLanguage();

    void languagereload_setTargetLanguage(String value);
}
