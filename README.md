# text-mask

Simple TextWatcher provider for applying masks.

Add to your build.gradle:
```gradle
repositories {
    maven { url "https://jitpack.io" }
}


dependencies {
    compile 'com.github.rafaelgsm:text-mask:v1.0'
}
```

## Usage

### To apply a mask to an EditText:
```java
EditText editText = (EditText) findViewById(R.id.edit_text);
TextWatcher textWatcher = TextMask.getWatcher("###.###.###-##", editText);
editText.addTextChangedListener(textWatcher);
```
Or:
```java
EditText editText = (EditText) findViewById(R.id.edit_text);
editText.addTextChangedListener(TextMask.getWatcher("###.###.###-##", editText));
```
Or:
```java
EditText editText = (EditText) findViewById(R.id.edit_text);
editText.addTextChangedListener(TextMask.getWatcher("+55 (##) #####-####", editText));
```

You can also use the mask to limit characters:
```java
TextMask.getWatcher("+55 (##) #####-####", editText, true);
```


### To get a masked text:
```java
String cleanString = "81999999999";
String maskedString = TextMask.getMaskedText("+55 (##) #####-####", cleanString);
```

### To get an unmasked text:
```java
String maskedString = "+55 (81) 99999-9999";
String cleanString = TextMask.getUnmaskedText("+55 (##) #####-####", maskedString);
```
