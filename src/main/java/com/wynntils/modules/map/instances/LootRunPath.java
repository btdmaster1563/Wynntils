/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.instances;

import com.wynntils.core.utils.objects.CubicSplines;
import com.wynntils.core.utils.objects.Location;
import net.minecraft.util.math.BlockPos;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.util.*;

public class LootRunPath {

    private CubicSplines.Spline3D spline;
    private LinkedHashSet<BlockPos> chests;

    private transient List<Location> lastSmoothSample;
    private transient List<Vector3d> lastSmoothDerivative;
    private transient List<Location> lastRoughSample;
    private transient List<Vector3d> lastRoughDerivative;

    public LootRunPath(Collection<? extends Point3d> points, Collection<? extends BlockPos> chests) {
        this.spline = new CubicSplines.Spline3D(points);
        this.chests = new LinkedHashSet<>(chests == null ? 11 : Math.max(2 * chests.size(), 11));
        if (chests != null) {
            for (BlockPos pos : chests) {
                this.chests.add(pos.toImmutable());
            }
        }
    }

    public LootRunPath() {
        this(Collections.emptyList(), Collections.emptyList());
    }

    public void addPoint(Point3d loc) {
        changed();
        spline.addPoint(loc);
    }

    public void addPoints(Collection<? extends Point3d> points) {
        changed();
        spline.addPoints(points);
    }

    public void addPointToFront(Location loc) {
        addPointsToFront(Collections.singletonList(loc));
    }

    public void addPointsToFront(Collection<? extends Point3d> points) {
        changed();
        spline.addPoints(0, points);
    }

    public void addChest(BlockPos loc) {
        chests.add(loc.toImmutable());
    }

    public Set<BlockPos> getChests() {
        return Collections.unmodifiableSet(chests);
    }

    public List<Location> getPoints() {
        return spline.getPoints();
    }

    public Location getLastPoint() {
        List<Location> points = getPoints();
        return points.isEmpty() ? null : points.get(points.size() - 1);
    }

    private void changed() {
        lastSmoothSample = null;
        lastSmoothDerivative = null;
        lastRoughSample = null;
        lastRoughDerivative = null;
    }

    public List<Location> getSmoothPoints() {
        if (lastSmoothSample == null) {
            lastSmoothSample = spline.sample();
        }
        return lastSmoothSample;
    }

    public List<Vector3d> getSmoothDirections() {
        if (lastSmoothDerivative == null) {
            lastSmoothDerivative = spline.sampleDerivative();
        }
        return lastSmoothDerivative;
    }

    public List<Location> getRoughPoints() {
        if (lastRoughSample == null) {
            lastRoughSample = spline.sample(1);
        }
        return lastRoughSample;
    }

    public List<Vector3d> getRoughDirections() {
        if (lastRoughDerivative == null) {
            lastRoughDerivative = spline.sampleDerivative(1);
        }
        return lastRoughDerivative;
    }

    public boolean isEmpty() {
        return getPoints().isEmpty();
    }

}
