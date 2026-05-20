package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeoUtils {

    private static final Pattern COORD_PATTERN =
            Pattern.compile("\"lat\":\\s*([\\d.\\-]+)[\\s,\"]*\"lng\":\\s*([\\d.\\-]+)");

    private GeoUtils() {
    }


    public static void validateKoordinat(String koordinatJson) {
        if (koordinatJson == null || koordinatJson.isBlank()) {
            throw new IllegalArgumentException("Koordinat is required");
        }

        List<double[]> points = parsePoints(koordinatJson);

        if (points.size() != 4) {
            throw new IllegalArgumentException(
                    "Kebun must have exactly 4 coordinate points, got "
                            + points.size());
        }

        if (!isRectangle(points)) {
            throw new IllegalArgumentException(
                    "Kebun coordinates must form a rectangle");
        }
    }

    public static boolean isOverlapping(String coord1, String coord2) {
        if (coord1 == null || coord2 == null) {
            return false;
        }

        double[] bounds1 = computeBoundingBox(coord1);
        double[] bounds2 = computeBoundingBox(coord2);

        if (bounds1 == null || bounds2 == null) {
            return false;
        }

        boolean overlapLat = bounds1[1] > bounds2[0] && bounds1[0] < bounds2[1];
        boolean overlapLng = bounds1[3] > bounds2[2] && bounds1[2] < bounds2[3];

        return overlapLat && overlapLng;
    }

    public static List<double[]> parsePoints(String koordinatJson) {
        List<double[]> points = new ArrayList<>();
        Matcher matcher = COORD_PATTERN.matcher(koordinatJson);

        while (matcher.find()) {
            double lat = Double.parseDouble(matcher.group(1));
            double lng = Double.parseDouble(matcher.group(2));
            points.add(new double[]{lat, lng});
        }

        return points;
    }

    static double[] computeBoundingBox(String koordinatJson) {
        List<double[]> points = parsePoints(koordinatJson);
        if (points.isEmpty()) {
            return null;
        }

        double minLat = Double.MAX_VALUE;
        double maxLat = -Double.MAX_VALUE;
        double minLng = Double.MAX_VALUE;
        double maxLng = -Double.MAX_VALUE;

        for (double[] point : points) {
            minLat = Math.min(minLat, point[0]);
            maxLat = Math.max(maxLat, point[0]);
            minLng = Math.min(minLng, point[1]);
            maxLng = Math.max(maxLng, point[1]);
        }

        return new double[]{minLat, maxLat, minLng, maxLng};
    }

    private static boolean isRectangle(List<double[]> points) {
        double cx = points.stream().mapToDouble(p -> p[0]).average().orElse(0);
        double cy = points.stream().mapToDouble(p -> p[1]).average().orElse(0);

        double[] distances = points.stream()
                .mapToDouble(p -> (p[0] - cx) * (p[0] - cx)
                        + (p[1] - cy) * (p[1] - cy))
                .toArray();

        double first = distances[0];
        return Arrays.stream(distances).allMatch(d -> Math.abs(d - first) < 1e-6);
    }
}
