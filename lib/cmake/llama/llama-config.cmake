set(LLAMA_VERSION      0.0.4242)
set(LLAMA_BUILD_COMMIT 642330ac)
set(LLAMA_BUILD_NUMBER 4242)
set(LLAMA_SHARED_LIB   ON)

set(GGML_STATIC OFF)
set(GGML_NATIVE ON)
set(GGML_LTO    OFF)
set(GGML_CCACHE ON)
set(GGML_AVX    OFF)
set(GGML_AVX2   OFF)
set(GGML_AVX512 OFF)
set(GGML_AVX512_VBMI OFF)
set(GGML_AVX512_VNNI OFF)
set(GGML_AVX512_BF16 OFF)
set(GGML_AMX_TILE OFF)
set(GGML_AMX_INT8 OFF)
set(GGML_AMX_BF16 OFF)
set(GGML_FMA  OFF)
set(GGML_LASX ON)
set(GGML_LSX  ON)
set(GGML_RVV  ON)
set(GGML_SVE  OFF)

set(GGML_ACCELERATE ON)
set(GGML_OPENMP  ON)
set(GGML_CPU_HBM OFF)
set(GGML_BLAS_VENDOR Generic)

set(GGML_CUDA_FORCE_MMQ    OFF)
set(GGML_CUDA_FORCE_CUBLAS OFF)
set(GGML_CUDA_F16          OFF)
set(GGML_CUDA_PEER_MAX_BATCH_SIZE 128)
set(GGML_CUDA_NO_PEER_COPY  OFF)
set(GGML_CUDA_NO_VMM        OFF)
set(GGML_CUDA_FA_ALL_QUANTS OFF)
set(GGML_CUDA_GRAPHS        ON)

set(GGML_HIP_UMA OFF)

set(GGML_VULKAN_CHECK_RESULTS OFF)
set(GGML_VULKAN_DEBUG         OFF)
set(GGML_VULKAN_MEMORY_DEBUG  OFF)
set(GGML_VULKAN_SHADER_DEBUG_INFO OFF)
set(GGML_VULKAN_PERF      OFF)
set(GGML_VULKAN_VALIDATE  OFF)
set(GGML_VULKAN_RUN_TESTS OFF)

set(GGML_METAL_USE_BF16 OFF)
set(GGML_METAL_NDEBUG   OFF)
set(GGML_METAL_SHADER_DEBUG  OFF)
set(GGML_METAL_EMBED_LIBRARY OFF)
set(GGML_METAL_MACOSX_VERSION_MIN )
set(GGML_METAL_STD )

set(GGML_SYCL_F16    OFF)
set(GGML_SYCL_TARGET INTEL)
set(GGML_SYCL_DEVICE_ARCH )



####### Expanded from @PACKAGE_INIT@ by configure_package_config_file() #######
####### Any changes to this file will be overwritten by the next CMake run ####
####### The input file was llama-config.cmake.in                            ########

get_filename_component(PACKAGE_PREFIX_DIR "${CMAKE_CURRENT_LIST_DIR}/../../../" ABSOLUTE)

macro(set_and_check _var _file)
  set(${_var} "${_file}")
  if(NOT EXISTS "${_file}")
    message(FATAL_ERROR "File or directory ${_file} referenced by variable ${_var} does not exist !")
  endif()
endmacro()

macro(check_required_components _NAME)
  foreach(comp ${${_NAME}_FIND_COMPONENTS})
    if(NOT ${_NAME}_${comp}_FOUND)
      if(${_NAME}_FIND_REQUIRED_${comp})
        set(${_NAME}_FOUND FALSE)
      endif()
    endif()
  endforeach()
endmacro()

####################################################################################

set_and_check(LLAMA_INCLUDE_DIR "${PACKAGE_PREFIX_DIR}/include")
set_and_check(LLAMA_LIB_DIR     "${PACKAGE_PREFIX_DIR}/lib")
set_and_check(LLAMA_BIN_DIR     "${PACKAGE_PREFIX_DIR}/bin")

find_package(Threads REQUIRED)

set(_llama_transient_defines "GGML_USE_CUDA=1;GGML_CUDA_DMMV=1;GGML_CUDA_FORCE_DMMV=1;GGML_SCHED_MAX_COPIES=4;$<$<CONFIG:Debug>:_GLIBCXX_ASSERTIONS>;_XOPEN_SOURCE=600;_GNU_SOURCE;GGML_USE_CPU;GGML_USE_CUDA;GGML_BUILD;GGML_SHARED")
set(_llama_link_deps "")
set(_llama_link_opts "")
foreach(_ggml_lib ggml ggml-base)
    string(REPLACE "-" "_" _ggml_lib_var "${_ggml_lib}_LIBRARY")
    find_library(${_ggml_lib_var} ${_ggml_lib}
        REQUIRED
        HINTS ${LLAMA_LIB_DIR}
        NO_CMAKE_FIND_ROOT_PATH
    )
    list(APPEND _llama_link_deps "${${_ggml_lib_var}}")
    message(STATUS "Found ${${_ggml_lib_var}}")
endforeach()

foreach(backend amx blas cann cpu cuda hip kompute metal musa rpc sycl vulkan)
    string(TOUPPER "GGML_${backend}" backend_id)
    set(_ggml_lib "ggml-${backend}")
    string(REPLACE "-" "_" _ggml_lib_var "${_ggml_lib}_LIBRARY")

    find_library(${_ggml_lib_var} ${_ggml_lib}
        HINTS ${LLAMA_LIB_DIR}
        NO_CMAKE_FIND_ROOT_PATH
    )
    if(${_ggml_lib_var})
        list(APPEND _llama_link_deps "${${_ggml_lib_var}}")
        set(${backend_id} ON)
        message(STATUS "Found backend ${${_ggml_lib_var}}")
    else()
        set(${backend_id} OFF)
    endif()
endforeach()

if (NOT LLAMA_SHARED_LIB)
    if (APPLE AND GGML_ACCELERATE)
        find_library(ACCELERATE_FRAMEWORK Accelerate REQUIRED)
        list(APPEND _llama_link_deps ${ACCELERATE_FRAMEWORK})
    endif()

    if (GGML_OPENMP)
        find_package(OpenMP REQUIRED)
        list(APPEND _llama_link_deps OpenMP::OpenMP_C OpenMP::OpenMP_CXX)
    endif()

    if (GGML_CPU_HBM)
        find_library(memkind memkind REQUIRED)
        list(APPEND _llama_link_deps memkind)
    endif()

    if (GGML_BLAS)
        find_package(BLAS REQUIRED)
        list(APPEND _llama_link_deps ${BLAS_LIBRARIES})
        list(APPEND _llama_link_opts ${BLAS_LINKER_FLAGS})
    endif()

    if (GGML_CUDA)
        find_package(CUDAToolkit REQUIRED)
    endif()

    if (GGML_METAL)
        find_library(FOUNDATION_LIBRARY Foundation REQUIRED)
        find_library(METAL_FRAMEWORK    Metal REQUIRED)
        find_library(METALKIT_FRAMEWORK MetalKit REQUIRED)
        list(APPEND _llama_link_deps ${FOUNDATION_LIBRARY}
                                     ${METAL_FRAMEWORK} ${METALKIT_FRAMEWORK})
    endif()

    if (GGML_VULKAN)
        find_package(Vulkan REQUIRED)
        list(APPEND _llama_link_deps Vulkan::Vulkan)
    endif()

    if (GGML_HIP)
        find_package(hip     REQUIRED)
        find_package(hipblas REQUIRED)
        find_package(rocblas REQUIRED)
        list(APPEND _llama_link_deps hip::host roc::rocblas roc::hipblas)
    endif()

    if (GGML_SYCL)
        find_package(DNNL)
        if (${DNNL_FOUND} AND GGML_SYCL_TARGET STREQUAL "INTEL")
            list(APPEND _llama_link_deps DNNL::dnnl)
        endif()
        if (WIN32)
            find_package(IntelSYCL REQUIRED)
            find_package(MKL       REQUIRED)
            list(APPEND _llama_link_deps IntelSYCL::SYCL_CXX MKL::MKL MKL::MKL_SYCL)
        endif()
    endif()
endif()

find_library(llama_LIBRARY llama
    REQUIRED
    HINTS ${LLAMA_LIB_DIR}
    NO_CMAKE_FIND_ROOT_PATH
)

add_library(llama UNKNOWN IMPORTED)
set_target_properties(llama
    PROPERTIES
        INTERFACE_INCLUDE_DIRECTORIES "${LLAMA_INCLUDE_DIR}"
        INTERFACE_LINK_LIBRARIES "${_llama_link_deps}"
        INTERFACE_LINK_OPTIONS   "${_llama_link_opts}"
        INTERFACE_COMPILE_DEFINITIONS "${_llama_transient_defines}"
        IMPORTED_LINK_INTERFACE_LANGUAGES "CXX"
        IMPORTED_LOCATION "${llama_LIBRARY}"
        INTERFACE_COMPILE_FEATURES cxx_std_11
        POSITION_INDEPENDENT_CODE ON )

check_required_components(Llama)
