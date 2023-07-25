package br.com.dsousasantos91.assembleia.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Arrays;
import java.util.Objects;

public class PropertyUtils {

    public static void copyNonNullProperties(Object src, Object target, String... ignoreProperties) {
        String[] nullPropertyNames = getNullPropertyNames(src);
        BeanUtils.copyProperties(src, target, concatenateArrays(ignoreProperties, nullPropertyNames));
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper srcWrapper = new BeanWrapperImpl(source);
        return Arrays.stream(srcWrapper.getPropertyDescriptors())
                     .map(pd -> srcWrapper.getPropertyValue(pd.getName()) == null ? pd.getName() : null)
                     .filter(Objects::nonNull)
                     .toArray(String[]::new);
    }

    private static String[] concatenateArrays(String[] arr1, String[] arr2) {
        String[] result = new String[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, result, 0, arr1.length);
        System.arraycopy(arr2, 0, result, arr1.length, arr2.length);
        return result;
    }
}
