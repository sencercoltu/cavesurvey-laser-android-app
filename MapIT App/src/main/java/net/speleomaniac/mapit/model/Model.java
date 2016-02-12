package net.speleomaniac.mapit.model;

import net.speleomaniac.mapit.StationData;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Sencer Coltu on 1.9.2014.
 */
public class Model {
    public final static float SMALL_NUM = 0.00000001f;
    public final ArrayList<Point> Points = new ArrayList<Point>();
    public final ArrayList<Surface> Surfaces = new ArrayList<Surface>();
    private final ArrayList<StationData> Stations;
    private Surface firstSurface = Surface.EmptySurface();
    private boolean firstSurfaceCreated = false;
    private int firstPointCounter = 0;
    private float xCenter = 0, yCenter = 0;

    public float[] selectedSegmentVertices = new float[6];
    public short[] selectedSegmentIndices = new short[] {0, 1};
    public FloatBuffer selectedSegmentVerticesBuffer;
    public FloatBuffer segmentColorsBuffer;
    public ShortBuffer selectedSegmentIndicesBuffer;


    public float[] segmentVertices = new float[0];
    public short[] segmentIndices = new short[0];
    public FloatBuffer segmentVerticesBuffer;
    public ShortBuffer segmentIndicesBuffer;


//    public float[] vertices = new float[0];
//    public short[] indices = new short[0];
//    public short[] normalIndices = new short[0];
//    public float[] normalVectors = new float[0];
//    public FloatBuffer vertexBuffer;
//    public FloatBuffer vertexNormalsBuffer;
//    public ShortBuffer vertexIndexBuffer;
//
//    public ShortBuffer normalIndexBuffer;
//    public FloatBuffer normalVectorBuffer;

    public Model(ArrayList<StationData> stations) {
        Stations = stations;

        ByteBuffer bb;

        bb = ByteBuffer.allocateDirect(2 * 2);
        bb.order(ByteOrder.nativeOrder());
        selectedSegmentIndicesBuffer = bb.asShortBuffer();
        selectedSegmentIndicesBuffer.put(selectedSegmentIndices);
        selectedSegmentIndicesBuffer.position(0);

        bb = ByteBuffer.allocateDirect(6 * 4);
        bb.order(ByteOrder.nativeOrder());
        selectedSegmentVerticesBuffer = bb.asFloatBuffer();
        selectedSegmentVerticesBuffer.put(selectedSegmentVertices);
        selectedSegmentVerticesBuffer.position(0);

    }

    public void selectSegment(StationData seg, int anchor) {
        if (seg == null) {
            return;
        }

        if (seg.Parent != null) {
            selectedSegmentVertices[0] = seg.Parent.Absolute.X;
            selectedSegmentVertices[1] = seg.Parent.Absolute.Y;
            selectedSegmentVertices[2] = seg.Parent.Absolute.Z;
        } else {
            selectedSegmentVertices[0] = 0;
            selectedSegmentVertices[1] = 0;
            selectedSegmentVertices[2] = 0;
        }
        selectedSegmentVertices[3] = seg.Absolute.X;
        selectedSegmentVertices[4] = seg.Absolute.Y;
        selectedSegmentVertices[5] = seg.Absolute.Z;
        selectedSegmentVerticesBuffer.put(selectedSegmentVertices);
        selectedSegmentVerticesBuffer.position(0);

        if (anchor == 0) {
            xCenter = selectedSegmentVertices[0];
            yCenter = selectedSegmentVertices[1];
        } else {
            xCenter = selectedSegmentVertices[3];
            yCenter = selectedSegmentVertices[4];
        }
    }

    public void reset() {
//        Points.clear();
//        Surfaces.clear();
//        firstSurface = Surface.EmptySurface();
//        firstSurfaceCreated = false;
//        firstPointCounter = 0;
//        vertices = new float[0];
//        indices = new short[0];
//        vertexBuffer = null;
//        vertexNormalsBuffer = null;
//        vertexIndexBuffer = null;
//        normalIndexBuffer = null;
//        normalVectorBuffer = null;

        xCenter = 0;
        yCenter = 0;

        selectedSegmentVertices[0] = 0;
        selectedSegmentVertices[1] = 0;
        selectedSegmentVertices[2] = 0;
        selectedSegmentVertices[3] = 0;
        selectedSegmentVertices[4] = 0;
        selectedSegmentVertices[5] = 0;
        selectedSegmentVerticesBuffer.put(selectedSegmentVertices);
        selectedSegmentVerticesBuffer.position(0);

        segmentVertices = new float[0];
        segmentIndices = new short[0];
        segmentIndicesBuffer = null;
        segmentVerticesBuffer = null;
        segmentColorsBuffer = null;
    }

    public void refreshModel() {
        int segmentCount = Stations.size();
        if (segmentCount == 0) return;
        int stationCount = segmentCount * 2 /*iki station var*/;
        int valueCount = segmentCount * 14 /* iki tane coord3 color4 */;

        segmentVertices = new float[valueCount];
        segmentIndices = new short[stationCount];

        int fidx = 0;
        int iidx = 0;
        StationData s;
        for (int i=0; i<segmentCount; i++) {
            s = Stations.get(i);

            if (s.Parent != null) {
                segmentVertices[fidx++] = s.Parent.Absolute.X;
                segmentVertices[fidx++] = s.Parent.Absolute.Y;
                segmentVertices[fidx++] = s.Parent.Absolute.Z;
            } else {
                segmentVertices[fidx++] = 0;
                segmentVertices[fidx++] = 0;
                segmentVertices[fidx++] = 0;
            }
            if (s.Type == StationData._branchShot) {
                segmentVertices[fidx++] = 1f;//r
                segmentVertices[fidx++] = 0f;//g
                segmentVertices[fidx++] = 0f;//b
                segmentVertices[fidx++] = 1f;//a
            } else if (s.Type == StationData._wallShot) {
                segmentVertices[fidx++] = 0f;//r
                segmentVertices[fidx++] = 0f;//g
                segmentVertices[fidx++] = 1f;//b
                segmentVertices[fidx++] = 1f;//a
            } else {
                segmentVertices[fidx++] = 1f;//r
                segmentVertices[fidx++] = 1f;//g
                segmentVertices[fidx++] = 1f;//b
                segmentVertices[fidx++] = 1f;//a
            }
            segmentVertices[fidx++] = s.Absolute.X;
            segmentVertices[fidx++] = s.Absolute.Y;
            segmentVertices[fidx++] = s.Absolute.Z;
            if (s.Type == StationData._branchShot) {
                segmentVertices[fidx++] = 1f;//r
                segmentVertices[fidx++] = 0f;//g
                segmentVertices[fidx++] = 0f;//b
                segmentVertices[fidx++] = 1f;//a
            } else if (s.Type == StationData._wallShot) {
                segmentVertices[fidx++] = 0f;//r
                segmentVertices[fidx++] = 0f;//g
                segmentVertices[fidx++] = 1f;//b
                segmentVertices[fidx++] = 1f;//a
            } else {
                segmentVertices[fidx++] = 1f;//r
                segmentVertices[fidx++] = 1f;//g
                segmentVertices[fidx++] = 1f;//b
                segmentVertices[fidx++] = 1f;//a
            }

            segmentIndices[iidx] = (short) iidx++;
            segmentIndices[iidx] = (short) iidx++;
        }

        ByteBuffer bb;

        bb = ByteBuffer.allocateDirect(stationCount * 2);
        bb.order(ByteOrder.nativeOrder());
        segmentIndicesBuffer = bb.asShortBuffer();
        segmentIndicesBuffer.put(segmentIndices);
        segmentIndicesBuffer.position(0);

        bb = ByteBuffer.allocateDirect(valueCount * 4);
        bb.order(ByteOrder.nativeOrder());
        segmentVerticesBuffer = bb.asFloatBuffer();
        segmentVerticesBuffer.put(segmentVertices);
        segmentVerticesBuffer.position(0);

        segmentColorsBuffer = segmentVerticesBuffer.duplicate();
        segmentColorsBuffer.position(3);

//        int triangles = Surfaces.size();
//        if (triangles == 0) return;
//        int vertexCount = triangles * 3;
//        int coords = vertexCount * 3 * 4;
//
//        // number of coordinates per vertex in this array
//        vertices = new float[coords];
//        normalVectors = new float[coords];
//        indices = new short[vertexCount];
//        normalIndices = new short[vertexCount * 2];
//
//        for (int i = 0; i < triangles; i++) {
//            Surface s = Surfaces.get(i);
//
//            int p = i*9*2;
//            normalVectors[p] = vertices[p++] = s.P0().X;
//            normalVectors[p] = vertices[p++] = s.P0().Y;
//            normalVectors[p] = vertices[p++] = s.P0().Z;
//
//            normalVectors[p] = s.P0().X + s.N0.X * 0.2f; vertices[p++] = s.N0.X;
//            normalVectors[p] = s.P0().Y + s.N0.Y * 0.2f; vertices[p++] = s.N0.Y;
//            normalVectors[p] = s.P0().Z + s.N0.Z * 0.2f; vertices[p++] = s.N0.Z;
//
//            normalVectors[p] = vertices[p++] = s.P1().X;
//            normalVectors[p] = vertices[p++] = s.P1().Y;
//            normalVectors[p] = vertices[p++] = s.P1().Z;
//
//            normalVectors[p] = s.P1().X + s.N1.X * 0.2f; vertices[p++] = s.N1.X;
//            normalVectors[p] = s.P1().Y + s.N1.Y * 0.2f; vertices[p++] = s.N1.Y;
//            normalVectors[p] = s.P1().Z + s.N1.Z * 0.2f; vertices[p++] = s.N1.Z;
//
//            normalVectors[p] = vertices[p++] = s.P2().X;
//            normalVectors[p] = vertices[p++] = s.P2().Y;
//            normalVectors[p] = vertices[p++] = s.P2().Z;
//
//            normalVectors[p] = s.P2().X + s.N2.X * 0.2f; vertices[p++] = s.N2.X;
//            normalVectors[p] = s.P2().Y + s.N2.Y * 0.2f; vertices[p++] = s.N2.Y;
//            normalVectors[p] = s.P2().Z + s.N2.Z * 0.2f; vertices[p] = s.N2.Z;
//
//            int k = i*3;
//            indices[k] = (short)(k);
//            indices[++k] = (short)(k);
//            indices[++k] = (short)(k);
//
//            k = i*3*2;
//            normalIndices[k] = (short)(k);
//            normalIndices[++k] = (short)(k);
//            normalIndices[++k] = (short)(k);
//            normalIndices[++k] = (short)(k);
//            normalIndices[++k] = (short)(k);
//            normalIndices[++k] = (short)(k);
//        }
//
//        ByteBuffer bb = ByteBuffer.allocateDirect(vertexCount * 2);
//        bb.order(ByteOrder.nativeOrder());
//        vertexIndexBuffer = bb.asShortBuffer();
//        vertexIndexBuffer.put(indices);
//        vertexIndexBuffer.position(0);
//
//        bb = ByteBuffer.allocateDirect(vertexCount * 2 * 2);
//        bb.order(ByteOrder.nativeOrder());
//        normalIndexBuffer = bb.asShortBuffer();
//        normalIndexBuffer.put(normalIndices);
//        normalIndexBuffer.position(0);
//
//        bb = ByteBuffer.allocateDirect(coords * 4 * 2);
//        bb.order(ByteOrder.nativeOrder());
//        vertexBuffer = bb.asFloatBuffer();
//        vertexBuffer.put(vertices);
//        vertexBuffer.position(0);
//
//        vertexNormalsBuffer = vertexBuffer.duplicate();
//        vertexNormalsBuffer.position(3);
//
//        bb = ByteBuffer.allocateDirect(coords * 4 * 2);
//        bb.order(ByteOrder.nativeOrder());
//        normalVectorBuffer = bb.asFloatBuffer();
//        normalVectorBuffer.put(normalVectors);
//        normalVectorBuffer.position(0);

    }

    public void draw(GL10 gl) {
        //if (indices.length == 0) return;
        if (segmentIndices.length == 0) return;

        gl.glTranslatef(-xCenter, -yCenter, 0);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);


        gl.glDisable(GL10.GL_LIGHTING);

        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glLineWidth(1f);
        gl.glColorPointer(4, GL10.GL_FLOAT, 28, segmentColorsBuffer);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 28, segmentVerticesBuffer);
        gl.glDrawElements(GL10.GL_LINES, segmentIndices.length, GL10.GL_UNSIGNED_SHORT, segmentIndicesBuffer);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

        gl.glLineWidth(3f);
        gl.glColor4f(0f, 1f, 0f, 1f);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 12, selectedSegmentVerticesBuffer);
        gl.glDrawElements(GL10.GL_LINES, selectedSegmentIndices.length, GL10.GL_UNSIGNED_SHORT, selectedSegmentIndicesBuffer);

        gl.glEnable(GL10.GL_LIGHTING);
//        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
//        gl.glVertexPointer(3, GL10.GL_FLOAT, 24, vertexBuffer);
//        gl.glNormalPointer(GL10.GL_FLOAT, 24, vertexNormalsBuffer);
//        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, vertexIndexBuffer);
//        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
//
//        gl.glColor4f(1f, 0f, 0f, 1f);
//        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, normalVectorBuffer);
//        gl.glDrawElements(GL10.GL_LINES, normalIndices.length, GL10.GL_UNSIGNED_SHORT, normalIndexBuffer);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    public void addRay(Ray r) {
        //pointu clouda ekle


        if (firstPointCounter > 2) {

            Points.add(r.P1);

            Surface s = null;
            Point p = Point.EmptyPoint();
            //tek surface cross edebilir, ondan ilkini alalım
            for (int i = 0; i < Surfaces.size(); i++) {
                s = Surfaces.get(i);
                if (intersect(r, s, p)) {
                    break;
                }
                s = null;
            }

            if (s != null) {
                //kesişti,
            } else {
                //kesişemedi, yeni point ekle sisteme
                //en yakın pointu bul
                p = closestPoint(r.P1);

                //en yakınıyla segment oluştur
                Segment newSeg = new Segment(r.P1, p);

                for (int i = 0; i < p.Segments.size(); i++) {
                    Segment seg = p.Segments.get(i);
                    //yeni segmentimizse devam et
                    if (seg == newSeg) continue;

                    Segment seg2 = null;
                    //segmentin iki yanı da surface ise devam et
                    if (seg.S0 != null && seg.S1 != null) continue;
                    if (seg.S0 == null) {
                        if (seg.P0 == p) {
                            seg2 = new Segment(r.P1, seg.P1);
                        }
                        else if (seg.P1 == p) {
                            seg2 = new Segment(r.P1, seg.P0);
                        }
                        Surfaces.add(new Surface(newSeg, seg, seg2));
                    }
                    if (seg.S1 == null) {
                        if (seg.P0 == p) {
                            seg2 = new Segment(r.P1, seg.P1);
                        }
                        else if (seg.P1 == p) {
                            seg2 = new Segment(r.P1, seg.P0);
                        }
                        Surfaces.add(new Surface(newSeg, seg, seg2));
                    }
                }
            }
        }
        else if (firstPointCounter == 0) {
            firstSurface.S0.P0.X = r.P0.X + r.P1.X;
            firstSurface.S0.P0.Y = r.P0.Y + r.P1.Y;
            firstSurface.S0.P0.Z = r.P0.Z + r.P1.Z;
            Points.add(firstSurface.S0.P0);
            firstSurface.S2.P1 = firstSurface.S0.P0;
            firstSurface.S0.P0.addSegment(firstSurface.S0);
            firstSurface.S0.P0.addSegment(firstSurface.S2);
        }
        else if (firstPointCounter == 1) {
            firstSurface.S1.P0.X = r.P0.X + r.P1.X;
            firstSurface.S1.P0.Y = r.P0.Y + r.P1.Y;
            firstSurface.S1.P0.Z = r.P0.Z + r.P1.Z;
            Points.add(firstSurface.S1.P0);
            firstSurface.S0.P1 = firstSurface.S1.P0;
            firstSurface.S1.P0.addSegment(firstSurface.S1);
            firstSurface.S1.P0.addSegment(firstSurface.S0);
        }
        else if (firstPointCounter == 2) {
            firstSurface.S2.P0.X = r.P0.X + r.P1.X;
            firstSurface.S2.P0.Y = r.P0.Y + r.P1.Y;
            firstSurface.S2.P0.Z = r.P0.Z + r.P1.Z;
            Points.add(firstSurface.S2.P0);
            firstSurface.S1.P1 = firstSurface.S2.P0;
            firstSurface.S2.P0.addSegment(firstSurface.S2);
            firstSurface.S2.P0.addSegment(firstSurface.S1);

            firstSurface.S0.S0 = firstSurface;
            firstSurface.S1.S0 = firstSurface;
            firstSurface.S2.S0 = firstSurface;

            firstSurface.calculateNormals();
            Surfaces.add(firstSurface);
        }
        firstPointCounter++;

    }

    public void closeSurfaces(Point p) {

        //eğer açısı 180'den büyükse return
    }

    public Point closestPoint(Point point) {
        Point closestPoint = null;
        float nearestLength = Float.MAX_VALUE;
        for (int i = 0; i < Points.size(); i++) {
            Point p = Points.get(i);
            if (p == point) continue;
            Vector v = p.subtract(point);
            float length = v.length();
            if (length < nearestLength) {
                nearestLength = length;
                closestPoint = p;
            }
        }
        return closestPoint;
    }

    public boolean intersect(Ray R, Surface S, Point I) {
        Vector u, v, n;
        Vector dir, w0, w;
        float r, a, b;

        u = S.S0.P1.subtract(S.S0.P0);
        v = S.S2.P0.subtract(S.S0.P0);
        n = u.product(v);

        if (n.equals(Vector.NullVector))
            return false;

        dir = R.P1.subtract(R.P0);
        w0 = R.P0.subtract(S.S0.P0);
        a = -n.dot(w0);
        b = n.dot(dir);

        if (Math.abs(b) < SMALL_NUM) {
            if (a == 0) return false;
            else return false;
        }

        r = a / b;
        if (r < 0.0)
            return false;

        Point Ii = R.P0.add(dir.product(r));
        I.X = Ii.X;I.Y = Ii.Y;I.Z = Ii.Z;

        float uu, uv, vv, wu, wv, D;
        uu = u.dot(u);
        uv = u.dot(v);
        vv = v.dot(v);
        w = I.subtract(S.S0.P0);
        wu = w.dot(u);
        wv = w.dot(v);

        D = uv * uv - uu * vv;

        float s, t;
        s = (uv * wv - vv * wu) / D;
        if (s < 0.0 || s > 1.0) return false;
        t = (uv * wu - uu * wv) / D;
        if (t < 0.0 || (s + t) > 1.0) return false;
        return true;
    }
}
