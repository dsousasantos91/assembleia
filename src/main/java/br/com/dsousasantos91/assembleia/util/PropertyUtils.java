package br.com.dsousasantos91.assembleia.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Arrays;
import java.util.Objects;

public class PropertyUtils {

    public static void copyNonNullProperties(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper srcWrapper = new BeanWrapperImpl(source);
        return Arrays.stream(srcWrapper.getPropertyDescriptors())
                     .map(pd -> srcWrapper.getPropertyValue(pd.getName()) == null ? pd.getName() : null)
                     .filter(Objects::nonNull)
                     .toArray(String[]::new);
    }
}
