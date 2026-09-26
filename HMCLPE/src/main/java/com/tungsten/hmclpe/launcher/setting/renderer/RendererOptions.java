package com.tungsten.hmclpe.launcher.setting.renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RendererOptions {

    public static final String GL4ES_114 = "libgl4es_114.so";
    public static final String GL4ES_115 = "libgl4es_115.so";
    public static final String GL4ES_112 = "libGL112.so.1";
    public static final String GL4ES_113 = "libGL113.so.1";
    public static final String GL4ES_116 = "libGL115.so.1";
    public static final String VIRGL = "VirGL";
    public static final String VULKAN_ZINK = "vulkan_zink";
    public static final String VGPU = "libvgpu.so";
    public static final String GLIDE_GL4ES = "libGlideNuke.so";

    public static final String[] POJAV_RENDERS = new String[]{
            GL4ES_114,
            GL4ES_115,
            GL4ES_112,
            GL4ES_113,
            GL4ES_116,
            VIRGL,
            VULKAN_ZINK,
            VGPU,
            GLIDE_GL4ES
    };

    public static final String[] BOAT_RENDERS = new String[]{
            GL4ES_114,
            GL4ES_115,
            VIRGL,
            VGPU
    };

    private RendererOptions(){}

    public static String canonicalize(String renderer){
        if (renderer == null) return GL4ES_114;
        switch (renderer){
            case "opengles2":
            case "opengles2_5":
            case "opengles3":
            case "opengles3_vgpu":
            case "GL4ES114":
            case "GL4ES115":
                return GL4ES_114;
            case "opengles3_virgl":
                return VIRGL;
            case "vulkan_zink":
                return VULKAN_ZINK;
            case "libvgpu.so":
                return VGPU;
            case "libGL112.so.1":
                return GL4ES_112;
            case "libGL113.so.1":
                return GL4ES_113;
            case "libGL115.so.1":
                return GL4ES_116;
            case "libgl4es_114.so":
                return GL4ES_114;
            case "libgl4es_115.so":
                return GL4ES_115;
            case "VirGL":
                return VIRGL;
            default:
                return renderer;
        }
    }

    public static String displayName(String renderer){
        if (renderer == null) return "GL4ES";
        switch (renderer){
            case GL4ES_114: return "Holy GL4ES 114 (OpenGL 2.1/3.2)";
            case GL4ES_115: return "Holy GL4ES 115 (OpenGL 4.x)";
            case GL4ES_112: return "GL4ES 112 (OpenGL 2.0)";
            case GL4ES_113: return "GL4ES 113 (OpenGL 2.1)";
            case GL4ES_116: return "GL4ES 116 (OpenGL 4.3+)";
            case VIRGL: return "VirGLRenderer (OpenGL 4.3, Vulkan)";
            case VULKAN_ZINK: return "Vulkan Zink / OSMesa (Software GL)";
            case VGPU: return "vGPU Mali (OpenGL ES3.2 native)";
            case GLIDE_GL4ES: return "GlideNuke + GL4ES (Android 9+ GLES3)";
            default: return renderer;
        }
    }

    public static boolean isGl4es(String renderer){
        if (renderer == null) return true;
        return renderer.equals(GL4ES_114) || renderer.equals(GL4ES_115) || renderer.equals(GL4ES_112)
                || renderer.equals(GL4ES_113) || renderer.equals(GL4ES_116) || renderer.equals(GLIDE_GL4ES);
    }

    public static boolean isVirGL(String renderer){
        return renderer != null && renderer.equals(VIRGL);
    }

    public static boolean isZink(String renderer){
        return renderer != null && renderer.equals(VULKAN_ZINK);
    }

    public static boolean isVGpu(String renderer){
        return renderer != null && renderer.equals(VGPU);
    }

    public static String getLibraryForBoat(String renderer){
        String r = canonicalize(renderer);
        if (r.equals(VIRGL) || r.equals(VULKAN_ZINK)) return "virgl";
        return "gl4es";
    }

    public static String getLwjglLibNameForBoat(String renderer){
        String r = canonicalize(renderer);
        if (r.equals(VIRGL) || r.equals(VULKAN_ZINK)) return "libGL.so.1";
        if (r.equals(GL4ES_115)) return "libgl4es_115.so";
        if (r.equals(GL4ES_112)) return "libGL112.so.1";
        if (r.equals(GL4ES_113)) return "libGL113.so.1";
        if (r.equals(GL4ES_116)) return "libGL115.so.1";
        if (r.equals(VGPU)) return "libvgpu.so";
        if (r.equals(GLIDE_GL4ES)) return "libGlideNuke.so";
        return "libgl4es_114.so";
    }

    public static String getGraphicsLibraryForPojav(String renderer){
        String r = canonicalize(renderer);
        switch (r){
            case VIRGL:
            case VULKAN_ZINK:
                return "libOSMesa_8.so";
            case VGPU:
                return "libgl4es_114.so";
            case GLIDE_GL4ES:
                return "libgl4es_114.so";
            case GL4ES_115:
                return "libgl4es_115.so";
            default:
                return "libgl4es_114.so";
        }
    }

    public static List<String> optionsForPojav(){
        return new ArrayList<>(java.util.Arrays.asList(POJAV_RENDERS));
    }

    public static List<String> optionsForBoat(){
        return new ArrayList<>(java.util.Arrays.asList(BOAT_RENDERS));
    }

    public static String defaultRenderer(){
        return GL4ES_114;
    }
}
